-- WARNING: This schema is for context only and is not meant to be run.
-- Table order and constraints may not be valid for execution.

CREATE SEQUENCE IF NOT EXISTS local_id_seq;
CREATE SEQUENCE IF NOT EXISTS usuario_id_seq;

CREATE TABLE public.usuario (
  id integer NOT NULL DEFAULT nextval('usuario_id_seq'::regclass),
  nome character varying NOT NULL,
  CONSTRAINT usuario_pkey PRIMARY KEY (id)
);

CREATE TABLE public.categoria (
  id integer NOT NULL DEFAULT nextval('categoria_id_seq'::regclass),
  id_publico character varying NOT NULL UNIQUE,
  nome character varying NOT NULL,
  tipo character varying NOT NULL CHECK (tipo::text = ANY (ARRAY['Departamento'::character varying, 'Categoria'::character varying]::text[])),
  id_categoria_pai integer,
  CONSTRAINT categoria_pkey PRIMARY KEY (id),
  CONSTRAINT categoria_id_categoria_pai_fkey FOREIGN KEY (id_categoria_pai) REFERENCES public.categoria(id)
);
CREATE TABLE public.estoque (
  id_estoque integer NOT NULL DEFAULT nextval('estoque_id_estoque_seq'::regclass),
  id_produto integer NOT NULL,
  quantidade integer NOT NULL DEFAULT 0 CHECK (quantidade >= 0),
  id_local integer NOT NULL,
  ultima_atualizacao timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT estoque_pkey PRIMARY KEY (id_estoque),
  CONSTRAINT estoque_id_produto_fkey FOREIGN KEY (id_produto) REFERENCES public.produto(id_produto),
  CONSTRAINT estoque_id_local_fkey FOREIGN KEY (id_local) REFERENCES public.local_estoque(id_local)
);
CREATE TABLE public.local_estoque (
  id_local integer NOT NULL DEFAULT nextval('local_id_seq'::regclass),
  nome character varying(80) NOT NULL,
  descricao character varying(255),
  CONSTRAINT local_estoque_pkey PRIMARY KEY (id_local),
  CONSTRAINT uq_local_nome UNIQUE (nome)
);
CREATE TABLE public.item_venda (
  id_item_venda integer NOT NULL DEFAULT nextval('item_venda_id_item_venda_seq'::regclass),
  id_venda integer NOT NULL,
  id_produto integer NOT NULL,
  quantidade integer NOT NULL CHECK (quantidade > 0),
  preco_unitario numeric NOT NULL CHECK (preco_unitario > 0::numeric),
  desconto numeric DEFAULT 0 CHECK (desconto >= 0::numeric),
  CONSTRAINT item_venda_pkey PRIMARY KEY (id_item_venda),
  CONSTRAINT item_venda_id_venda_fkey FOREIGN KEY (id_venda) REFERENCES public.venda(id_venda),
  CONSTRAINT item_venda_id_produto_fkey FOREIGN KEY (id_produto) REFERENCES public.produto(id_produto)
);
CREATE TABLE public.movimentacao_estoque (
  id_movimentacao integer NOT NULL DEFAULT nextval('movimentacao_estoque_id_movimentacao_seq'::regclass),
  id_produto integer NOT NULL,
  tipo character varying NOT NULL CHECK (tipo::text = ANY (ARRAY['entrada'::character varying, 'saida'::character varying, 'ajuste'::character varying]::text[])),
  quantidade integer NOT NULL CHECK (quantidade > 0),
  data_movimentacao timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
  referencia character varying,
  id_usuario integer,
  CONSTRAINT movimentacao_estoque_pkey PRIMARY KEY (id_movimentacao),
  CONSTRAINT movimentacao_estoque_id_produto_fkey FOREIGN KEY (id_produto) REFERENCES public.produto(id_produto),
  CONSTRAINT movimentacao_estoque_id_usuario_fkey FOREIGN KEY (id_usuario) REFERENCES public.usuario(id)
);
CREATE TABLE public.produto (
  id_produto integer NOT NULL DEFAULT nextval('produto_id_produto_seq'::regclass),
  nome character varying NOT NULL,
  descricao text,
  data_validade date,
  data_cadastro timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
  ativo boolean DEFAULT true,
  id_categoria integer NOT NULL,
  CONSTRAINT produto_pkey PRIMARY KEY (id_produto),
  CONSTRAINT produto_id_categoria_fkey FOREIGN KEY (id_categoria) REFERENCES public.categoria(id)
);
CREATE TABLE public.produto_imagem (
  id_imagem integer NOT NULL DEFAULT nextval('produto_imagem_id_imagem_seq'::regclass),
  id_produto integer NOT NULL,
  nome character varying NOT NULL,
  url character varying NOT NULL,
  path character varying NOT NULL,
  criado_em timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT produto_imagem_pkey PRIMARY KEY (id_imagem),
  CONSTRAINT produto_imagem_id_produto_fkey FOREIGN KEY (id_produto) REFERENCES public.produto(id_produto)
);
CREATE TABLE public.venda (
  id_venda integer NOT NULL DEFAULT nextval('venda_id_venda_seq'::regclass),
  data_venda timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
  status character varying NOT NULL DEFAULT 'pendente'::character varying CHECK (status::text = ANY (ARRAY['concluída'::character varying, 'pendente'::character varying, 'rascunho'::character varying, 'cancelada'::character varying]::text[])),
  estoque_processado boolean DEFAULT false,
  observacoes text,
  CONSTRAINT venda_pkey PRIMARY KEY (id_venda)
);
