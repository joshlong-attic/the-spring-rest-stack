create sequence  hibernate_sequence ;


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
);

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
  CONSTRAINT fk24217fdef32da70a FOREIGN KEY (customer_user_id_fkey)  REFERENCES user_account (id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION
);

INSERT INTO public.user_account (id, first_name, last_name, pass_word, signup_date, user_name, enabled, profile_photo_imported, profile_photo_media_type) VALUES (nextval('hibernate_sequence'), 'Clark', 'Kent', 'uberman', '2013-06-02 15:33:51', 'clarkkent', true, false, null);
INSERT INTO public.user_account (id, first_name, last_name, pass_word, signup_date, user_name, enabled, profile_photo_imported, profile_photo_media_type) VALUES (nextval('hibernate_sequence'), 'Lois', 'Lane', 'thebetterhalf', '2013-06-02 15:33:51', 'loislane', true, false, null);
INSERT INTO public.user_account (id, first_name, last_name, pass_word, signup_date, user_name, enabled, profile_photo_imported, profile_photo_media_type) VALUES (nextval('hibernate_sequence'), 'Bruce', 'Wayne', 'alfred', '2013-06-02 15:33:51', 'brucewayne', true, false, null);
INSERT INTO public.user_account (id, first_name, last_name, pass_word, signup_date, user_name, enabled, profile_photo_imported, profile_photo_media_type) VALUES (nextval('hibernate_sequence'), 'Tony', 'Stark', 'pepper', '2013-06-02 15:33:51', 'tonystark', true, false, null);
INSERT INTO public.user_account (id, first_name, last_name, pass_word, signup_date, user_name, enabled, profile_photo_imported, profile_photo_media_type) VALUES (nextval('hibernate_sequence'), 'Josh', 'Long', 'cowbell', '2013-06-02 15:33:51', 'joshlong', true, true, null);
INSERT INTO public.user_account (id, first_name, last_name, pass_word, signup_date, user_name, enabled, profile_photo_imported, profile_photo_media_type) VALUES (nextval('hibernate_sequence'), 'George', 'Jetson', 'sprockets', '2013-07-18 20:52:28', 'georgelee', true, false, null);

INSERT INTO public.customer (id, first_name, last_name, signup_date, customer_user_id_fkey) VALUES (nextval('hibernate_sequence'), 'arjen', 'poutsma', '2013-06-12 22:37:22', 3);
INSERT INTO public.customer (id, first_name, last_name, signup_date, customer_user_id_fkey) VALUES (nextval('hibernate_sequence'), 'mark', 'pollack', '2013-06-12 22:37:22', 4);
INSERT INTO public.customer (id, first_name, last_name, signup_date, customer_user_id_fkey) VALUES (nextval('hibernate_sequence'), 'david', 'syer', '2013-06-12 22:37:22', 2);
INSERT INTO public.customer (id, first_name, last_name, signup_date, customer_user_id_fkey) VALUES (nextval('hibernate_sequence'), 'gunnar', 'hillert', '2013-06-12 22:37:22', 2);
INSERT INTO public.customer (id, first_name, last_name, signup_date, customer_user_id_fkey) VALUES (nextval('hibernate_sequence'), 'rossen', 'stoyanchev', '2013-06-12 22:37:22', 2);
INSERT INTO public.customer (id, first_name, last_name, signup_date, customer_user_id_fkey) VALUES (nextval('hibernate_sequence'), 'josh', 'long', '2013-06-13 14:13:05', 2);
INSERT INTO public.customer (id, first_name, last_name, signup_date, customer_user_id_fkey) VALUES (nextval('hibernate_sequence'), 'brian', 'dussault', '2013-06-13 17:07:21', 3);
INSERT INTO public.customer (id, first_name, last_name, signup_date, customer_user_id_fkey) VALUES (nextval('hibernate_sequence'), 'phill', 'webb', '2013-06-26 03:54:53', 4);
INSERT INTO public.customer (id, first_name, last_name, signup_date, customer_user_id_fkey) VALUES (nextval('hibernate_sequence'), 'scott', 'andrews', '2013-06-28 19:11:19', 2);
INSERT INTO public.customer (id, first_name, last_name, signup_date, customer_user_id_fkey) VALUES (nextval('hibernate_sequence'), 'mark', 'fisher', '2013-06-28 19:54:14', 5);
INSERT INTO public.customer (id, first_name, last_name, signup_date, customer_user_id_fkey) VALUES (nextval('hibernate_sequence'), 'josh', 'long', '2013-07-15 17:12:46', 6);


-- insert into oauth_access_token (token_id, token, authentication_id, user_name, client_id, authentication, refresh_token) values (?, ?, ?, ?, ?, ?, ?)
create table oauth_access_token (
  token_id varchar ,
  token bytea ,
  authentication_id varchar ,
  user_name varchar ,
  client_id varchar ,
  authentication varchar ,
  refresh_token varchar  ,
  CONSTRAINT pk PRIMARY KEY (token_id )
) ;