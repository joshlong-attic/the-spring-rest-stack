--  schema for the CRM on the service side
--
CREATE TABLE user_account
(
  id bigint NOT NULL,
  first_name varchar (255),
  last_name varchar(255),
  pass_word varchar(255),
  signup_date timestamp  ,
  user_name varchar(255),
  enabled boolean NOT NULL DEFAULT false,
  profile_photo_imported boolean NOT NULL DEFAULT false,
  profile_photo_media_type varchar(255),
  CONSTRAINT user_account_pkey PRIMARY KEY (id )
)
;

-- update the customers
--
CREATE TABLE customer
(
  id bigint NOT NULL,
  first_name varchar(255),
  last_name varchar(255),
  signup_date timestamp  ,
  customer_user_id_fkey bigint NOT NULL,
  CONSTRAINT customer_pkey PRIMARY KEY (id ),
  CONSTRAINT fk24217fdef32da70a FOREIGN KEY (customer_user_id_fkey)
      REFERENCES user_account (id)
      ON UPDATE NO ACTION ON DELETE NO ACTION
);