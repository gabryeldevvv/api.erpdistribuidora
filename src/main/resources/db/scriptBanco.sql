-- =======================
-- 1. TABELAS PRINCIPAIS


-- Tabela de Local de Estoque (nova)
CREATE TABLE local_estoque (
    id_local SERIAL PRIMARY KEY,
    nome VARCHAR(80) NOT NULL UNIQUE,
    descricao VARCHAR(255)
);

-- =======================

-- Tabela de Produto (atualizada seguindo padrão de referência)
CREATE TABLE produto (
    id_produto SERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    descricao TEXT,
    preco_unitario DECIMAL(10, 2) NOT NULL,
    unidade_medida VARCHAR(10) NOT NULL,
    data_validade DATE,
    data_cadastro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ativo BOOLEAN DEFAULT TRUE,
    CHECK (preco_unitario > 0)
);

-- Tabela de Estoque (reestruturada)
CREATE TABLE estoque (
    id_estoque SERIAL PRIMARY KEY,
    id_produto INTEGER NOT NULL REFERENCES produto(id_produto) ON DELETE CASCADE,
    quantidade INTEGER NOT NULL DEFAULT 0,
    id_local INTEGER NOT NULL REFERENCES local_estoque(id_local) ON DELETE RESTRICT,
    ultima_atualizacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CHECK (quantidade >= 0),
    UNIQUE (id_produto, id_local)
);

-- Tabela de Venda (reorganizada)
CREATE TABLE venda (
    id_venda SERIAL PRIMARY KEY,
    data_venda TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(20) NOT NULL DEFAULT 'pendente',
    estoque_processado BOOLEAN DEFAULT FALSE,
    observacoes TEXT,
    CHECK (status IN ('concluída', 'pendente', 'rascunho', 'cancelada'))
);

-- Tabela de Item_Venda (padronizada)
CREATE TABLE item_venda (
    id_item_venda SERIAL PRIMARY KEY,
    id_venda INTEGER NOT NULL REFERENCES venda(id_venda) ON DELETE CASCADE,
    id_produto INTEGER NOT NULL REFERENCES produto(id_produto),
    quantidade INTEGER NOT NULL,
    preco_unitario DECIMAL(10, 2) NOT NULL,
    desconto DECIMAL(10, 2) DEFAULT 0,
    CHECK (quantidade > 0),
    CHECK (preco_unitario > 0),
    CHECK (desconto >= 0)
);

-- Tabela de Movimentação de Estoque (completa)
CREATE TABLE movimentacao_estoque (
    id_movimentacao SERIAL PRIMARY KEY,
    id_produto INTEGER NOT NULL REFERENCES produto(id_produto),
    tipo VARCHAR(10) NOT NULL,
    quantidade INTEGER NOT NULL,
    data_movimentacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    referencia VARCHAR(100),
    id_usuario INTEGER,
    CHECK (tipo IN ('entrada', 'saida', 'ajuste')),
    CHECK (quantidade > 0)
);

-- =======================
-- 2. ÍNDICES PARA PERFORMANCE
-- =======================

CREATE INDEX idx_estoque_produto ON estoque(id_produto);
CREATE INDEX idx_item_venda_produto ON item_venda(id_produto);
CREATE INDEX idx_item_venda_venda ON item_venda(id_venda);
CREATE INDEX idx_movimentacao_produto ON movimentacao_estoque(id_produto);
CREATE INDEX idx_movimentacao_data ON movimentacao_estoque(data_movimentacao);
CREATE INDEX idx_venda_status ON venda(status);

-- =======================
-- 3. FUNÇÃO + TRIGGER (PADRONIZADO)
-- =======================

CREATE OR REPLACE FUNCTION processar_estoque_apos_venda()
RETURNS TRIGGER AS $$
DECLARE
    item RECORD;
    estoque_atual INTEGER;
BEGIN
    -- Apenas processa se status for 'concluída' e ainda não processado
    IF NEW.status = 'concluída' AND NOT NEW.estoque_processado THEN
        -- Verifica se todos os itens têm estoque suficiente
        FOR item IN
            SELECT iv.id_produto, iv.quantidade
            FROM item_venda iv
            WHERE iv.id_venda = NEW.id_venda
        LOOP
            SELECT quantidade INTO estoque_atual
            FROM estoque
            WHERE id_produto = item.id_produto
            FOR UPDATE;

            IF estoque_atual IS NULL THEN
                RAISE EXCEPTION 'Produto ID % não está registrado no estoque.', item.id_produto;
            END IF;

            IF estoque_atual < item.quantidade THEN
                RAISE EXCEPTION 'Estoque insuficiente para o produto ID % (disponível: %, necessário: %).',
                    item.id_produto, estoque_atual, item.quantidade;
            END IF;
        END LOOP;

        -- Atualiza estoque e registra movimentações
        FOR item IN
            SELECT iv.id_produto, iv.quantidade
            FROM item_venda iv
            WHERE iv.id_venda = NEW.id_venda
        LOOP
            -- Atualizar estoque
            UPDATE estoque
            SET quantidade = quantidade - item.quantidade,
                ultima_atualizacao = CURRENT_TIMESTAMP
            WHERE id_produto = item.id_produto;

            -- Registrar movimentação
            INSERT INTO movimentacao_estoque (
                id_produto, tipo, quantidade, data_movimentacao, referencia
            ) VALUES (
                item.id_produto, 'saida', item.quantidade, CURRENT_TIMESTAMP,
                'Venda #' || NEW.id_venda
            );
        END LOOP;

        -- Marcar como processado
        NEW.estoque_processado := TRUE;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Trigger para processamento automático de estoque
CREATE TRIGGER tg_processar_estoque_apos_venda
AFTER INSERT OR UPDATE OF status ON venda
FOR EACH ROW
WHEN (NEW.status = 'concluída')
EXECUTE FUNCTION processar_estoque_apos_venda();

-- =======================
-- 4. COMENTÁRIOS ADICIONAIS
-- =======================

COMMENT ON TABLE produto IS 'Armazena informações dos produtos disponíveis para venda';
COMMENT ON COLUMN produto.preco_unitario IS 'Preço unitário do produto em reais (R$)';
COMMENT ON FUNCTION processar_estoque_apos_venda() IS 'Função para atualizar estoque automaticamente após confirmação de venda';