--  schema for the CRM on the service side
-- 
CREATE TABLE user_account
(
  id bigint NOT NULL,
  first_name character varying(255),
  last_name character varying(255),
  pass_word character varying(255),
  signup_date timestamp without time zone,
  user_name character varying(255),
  enabled boolean NOT NULL DEFAULT false,
  profile_photo_imported boolean NOT NULL DEFAULT false,
  profile_photo_media_type character varying(255),
  CONSTRAINT user_account_pkey PRIMARY KEY (id )
)
;

-- update the customers 
-- 
CREATE TABLE customer
(
  id bigint NOT NULL,
  first_name character varying(255),
  last_name character varying(255),
  signup_date timestamp without time zone,
  customer_user_id_fkey bigint NOT NULL,
  CONSTRAINT customer_pkey PRIMARY KEY (id ),
  CONSTRAINT fk24217fdef32da70a FOREIGN KEY (customer_user_id_fkey)
      REFERENCES user_account (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
);