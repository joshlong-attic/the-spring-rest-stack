create sequence  hibernate_sequence ;

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
  CONSTRAINT fk24217fdef32da70a FOREIGN KEY (customer_user_id_fkey) REFERENCES user_account (id) ON UPDATE NO ACTION ON DELETE NO ACTION
);

INSERT INTO user_account (id, first_name, last_name, pass_word, signup_date, user_name, enabled, profile_photo_imported, profile_photo_media_type) VALUES (NEXTVAL('hibernate_sequence'), 'Clark', 'Kent', 'uberman', '2013-06-02 15:33:51', 'clarkkent', true, false, null);
INSERT INTO user_account (id, first_name, last_name, pass_word, signup_date, user_name, enabled, profile_photo_imported, profile_photo_media_type) VALUES (NEXTVAL('hibernate_sequence'), 'Lois', 'Lane', 'thebetterhalf', '2013-06-02 15:33:51', 'loislane', true, false, null);
INSERT INTO user_account (id, first_name, last_name, pass_word, signup_date, user_name, enabled, profile_photo_imported, profile_photo_media_type) VALUES (NEXTVAL('hibernate_sequence'), 'Bruce', 'Wayne', 'alfred', '2013-06-02 15:33:51', 'brucewayne', true, false, null);
INSERT INTO user_account (id, first_name, last_name, pass_word, signup_date, user_name, enabled, profile_photo_imported, profile_photo_media_type) VALUES (NEXTVAL('hibernate_sequence'), 'Tony', 'Stark', 'pepper', '2013-06-02 15:33:51', 'tonystark', true, false, null);
INSERT INTO user_account (id, first_name, last_name, pass_word, signup_date, user_name, enabled, profile_photo_imported, profile_photo_media_type) VALUES (NEXTVAL('hibernate_sequence'), 'Josh', 'Long', 'cowbell', '2013-06-02 15:33:51', 'joshlong', true, true, 'image/png');
INSERT INTO user_account (id, first_name, last_name, pass_word, signup_date, user_name, enabled, profile_photo_imported, profile_photo_media_type) VALUES (NEXTVAL('hibernate_sequence'), 'George', 'Jetson', 'sprockets', '2013-07-18 20:52:28', 'georgelee', true, false, null);

INSERT INTO customer (id, first_name, last_name, signup_date, customer_user_id_fkey) VALUES (NEXTVAL('hibernate_sequence'), 'arjen', 'poutsma', '2013-06-12 22:37:22', 3);
INSERT INTO customer (id, first_name, last_name, signup_date, customer_user_id_fkey) VALUES (NEXTVAL('hibernate_sequence'), 'mark', 'pollack', '2013-06-12 22:37:22', 4);
INSERT INTO customer (id, first_name, last_name, signup_date, customer_user_id_fkey) VALUES (NEXTVAL('hibernate_sequence'), 'david', 'syer', '2013-06-12 22:37:22', 2);
INSERT INTO customer (id, first_name, last_name, signup_date, customer_user_id_fkey) VALUES (NEXTVAL('hibernate_sequence'), 'gunnar', 'hillert', '2013-06-12 22:37:22', 5);
INSERT INTO customer (id, first_name, last_name, signup_date, customer_user_id_fkey) VALUES (NEXTVAL('hibernate_sequence'), 'rossen', 'stoyanchev', '2013-06-12 22:37:22', 5);
INSERT INTO customer (id, first_name, last_name, signup_date, customer_user_id_fkey) VALUES (NEXTVAL('hibernate_sequence'), 'josh', 'long', '2013-06-13 14:13:05', 5);
INSERT INTO customer (id, first_name, last_name, signup_date, customer_user_id_fkey) VALUES (NEXTVAL('hibernate_sequence'), 'brian', 'dussault', '2013-06-13 17:07:21', 3);
INSERT INTO customer (id, first_name, last_name, signup_date, customer_user_id_fkey) VALUES (NEXTVAL('hibernate_sequence'), 'phill', 'webb', '2013-06-26 03:54:53', 4);
INSERT INTO customer (id, first_name, last_name, signup_date, customer_user_id_fkey) VALUES (NEXTVAL('hibernate_sequence'), 'scott', 'andrews', '2013-06-28 19:11:19', 2);
INSERT INTO customer (id, first_name, last_name, signup_date, customer_user_id_fkey) VALUES (NEXTVAL('hibernate_sequence'), 'mark', 'fisher', '2013-06-28 19:54:14', 5);
INSERT INTO customer (id, first_name, last_name, signup_date, customer_user_id_fkey) VALUES (NEXTVAL('hibernate_sequence'), 'josh', 'long', '2013-07-15 17:12:46', 6);
