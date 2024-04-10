CREATE TABLE tool
(
    id_tool character varying(36) NOT NULL,
    tx_name character varying(256) NOT NULL,
    tx_category character varying(128) NOT NULL,
    tx_description character varying(512) NOT NULL,
    tx_userId character varying(36) NOT NULL,
    CONSTRAINT tool_pkey PRIMARY KEY (id_tool)
);
