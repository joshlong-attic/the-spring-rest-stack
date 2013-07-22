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
  CONSTRAINT fk24217fdef32da70a FOREIGN KEY (customer_user_id_fkey)
      REFERENCES user_account (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
);

INSERT INTO public.user_account (id, first_name, last_name, pass_word, signup_date, user_name, enabled, profile_photo_imported, profile_photo_media_type) VALUES (1, 'Clark', 'Kent', 'uberman', '2013-06-02 15:33:51', 'clarkkent', true, false, null);
INSERT INTO public.user_account (id, first_name, last_name, pass_word, signup_date, user_name, enabled, profile_photo_imported, profile_photo_media_type) VALUES (2, 'Lois', 'Lane', 'thebetterhalf', '2013-06-02 15:33:51', 'loislane', true, false, null);
INSERT INTO public.user_account (id, first_name, last_name, pass_word, signup_date, user_name, enabled, profile_photo_imported, profile_photo_media_type) VALUES (3, 'Bruce', 'Wayne', 'alfred', '2013-06-02 15:33:51', 'brucewayne', true, false, null);
INSERT INTO public.user_account (id, first_name, last_name, pass_word, signup_date, user_name, enabled, profile_photo_imported, profile_photo_media_type) VALUES (4, 'Tony', 'Stark', 'pepper', '2013-06-02 15:33:51', 'tonystark', true, false, null);
INSERT INTO public.user_account (id, first_name, last_name, pass_word, signup_date, user_name, enabled, profile_photo_imported, profile_photo_media_type) VALUES (5, 'Josh', 'Long', 'cowbell', '2013-06-02 15:33:51', 'joshlong', true, true, null);
INSERT INTO public.user_account (id, first_name, last_name, pass_word, signup_date, user_name, enabled, profile_photo_imported, profile_photo_media_type) VALUES (6, 'George', 'Jetson', 'sprockets', '2013-07-18 20:52:28', 'georgelee', true, false, null);

INSERT INTO public.customer (id, first_name, last_name, signup_date, customer_user_id_fkey) VALUES (1, 'arjen', 'poutsma', '2013-06-12 22:37:22', 3);
INSERT INTO public.customer (id, first_name, last_name, signup_date, customer_user_id_fkey) VALUES (2, 'mark', 'pollack', '2013-06-12 22:37:22', 4);
INSERT INTO public.customer (id, first_name, last_name, signup_date, customer_user_id_fkey) VALUES (3, 'david', 'syer', '2013-06-12 22:37:22', 2);
INSERT INTO public.customer (id, first_name, last_name, signup_date, customer_user_id_fkey) VALUES (4, 'gunnar', 'hillert', '2013-06-12 22:37:22', 2);
INSERT INTO public.customer (id, first_name, last_name, signup_date, customer_user_id_fkey) VALUES (5, 'rossen', 'stoyanchev', '2013-06-12 22:37:22', 2);
INSERT INTO public.customer (id, first_name, last_name, signup_date, customer_user_id_fkey) VALUES (6, 'josh', 'long', '2013-06-13 14:13:05', 2);
INSERT INTO public.customer (id, first_name, last_name, signup_date, customer_user_id_fkey) VALUES (7, 'brian', 'dussault', '2013-06-13 17:07:21', 3);
INSERT INTO public.customer (id, first_name, last_name, signup_date, customer_user_id_fkey) VALUES (8, 'phill', 'webb', '2013-06-26 03:54:53', 4);
INSERT INTO public.customer (id, first_name, last_name, signup_date, customer_user_id_fkey) VALUES (9, 'scott', 'andrews', '2013-06-28 19:11:19', 2);
INSERT INTO public.customer (id, first_name, last_name, signup_date, customer_user_id_fkey) VALUES (10, 'mark', 'fisher', '2013-06-28 19:54:14', 5);
INSERT INTO public.customer (id, first_name, last_name, signup_date, customer_user_id_fkey) VALUES (11, 'josh', 'long', '2013-07-15 17:12:46', 6);
