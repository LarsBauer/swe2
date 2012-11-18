USE ${dbname.mysql};
SHOW WARNINGS;

	DROP TABLE IF EXISTS hibernate_sequence;
CREATE TABLE hibernate_sequence(
	next_val BIGINT NOT NULL PRIMARY KEY
);

	DROP TABLE IF EXISTS kunde;
CREATE TABLE kunde(
	k_id BIGINT NOT NULL PRIMARY KEY,
	name NVARCHAR(32) NOT NULL,
	vorname NVARCHAR(32) NOT NULL,
	geschlecht CHAR(1),
	newsletter TINYINT(1) NOT NULL,
	email NVARCHAR(128) NOT NULL,
	passwort NVARCHAR(32) NOT NULL,
	erzeugt TIMESTAMP NOT NULL,
	aktualisiert TIMESTAMP NOT NULL
);

	DROP TABLE IF EXISTS adresse;
CREATE TABLE adresse(
	ad_id BIGINT NOT NULL,
	plz CHAR(5) NOT NULL,
	ort NVARCHAR(32) NOT NULL,
	strasse NVARCHAR(32),
	hausnummer NVARCHAR(5),
	kunde_fk BIGINT NOT NULL REFERENCES kunde(knr),
	erzeugt TIMESTAMP NOT NULL,
	aktualisiert TIMESTAMP NOT NULL	
);

	DROP TABLE IF EXISTS bestellung;
CREATE TABLE bestellung(
	b_id BIGINT NOT NULL PRIMARY KEY,
	kunde_fk BIGINT NOT NULL REFERENCES kunde(k_id),
	erzeugt TIMESTAMP NOT NULL,
	aktualisiert TIMESTAMP NOT NULL,
	status VARCHAR(32)
);

	DROP TABLE IF EXISTS bestellposition;
CREATE TABLE bestellposition(
	bp_id BIGINT NOT NULL PRIMARY KEY,
	bestellung_fk BIGINT NOT NULL REFERENCES bestellung(b_id),
	artikel_fk BIGINT NOT NULL REFERENCES artikel(a_id),
	anzahl SMALLINT NOT NULL,
);

	DROP TABLE IF EXISTS artikel;
CREATE TABLE artikel (
	a_id BIGINT NOT NULL PRIMARY KEY,
	name NVARCHAR(32) NOT NULL,
	preis DOUBLE NOT NULL,
	groesse VARCHAR(3),
	erzeugt TIMESTAMP NOT NULL,
	aktualisiert TIMESTAMP NOT NULL
);

	DROP TABLE IF EXISTS kreditkarte;
CREATE TABLE kreditkarte(
	kr_id BIGINT NOT NULL PRIMARY KEY,
	kreditkartennr NVARCHAR(16) NOT NULL,
	anbieter NVARCHAR(32) NOT NULL,
	sicherheitscode CHAR(3) NOT NULL,
	gueltigbis NVARCHAR(5) NOT NULL,
	kunde_fk BIGINT NOT NULL REFERENCES kunde(k_id),
	erzeugt TIMESTAMP NOT NULL,
	aktualisiert TIMESTAMP NOT NULL

);

DROP TABLE IF EXISTS bestellung_versand;
CREATE TABLE bestellung_versand(
	bestellung_fk BIGINT NOT NULL REFERENCES bestellung(b_id),
	versand_fk BIGINT NOT NULL REFERENCES versand(v_id),
	PRIMARY KEY(bestellung_fk, versand_fk)
);

	DROP TABLE IF EXISTS versand;
CREATE TABLE versand(
	v_id BIGINT NOT NULL,
	versandart NVARCHAR(32) NOT NULL,
	versandkosten DOUBLE,
	erzeugt TIMESTAMP NOT NULL,
	aktualisiert TIMESTAMP NOT NULL
);
