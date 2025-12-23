START TRANSACTION;

DROP TABLE IF EXISTS notes;

CREATE TABLE notes (
    id serial NOT NULL,
    title TEXT,
    content TEXT,
    created_at TIMESTAMP DEFAULT NOW(),
    CONSTRAINT PK_notes PRIMARY KEY (id)
);

CREATE TABLE photos(
    id serial NOT NULL,
    category VARCHAR (50) NOT NULL,
    img_url VARCHAR NOT NULL,
    CONSTRAINT PK_photos PRIMARY KEY (id)
);

CREATE TABLE timeline(
    id serial NOT NULL,
    date VARCHAR NOT NULL,
    title VARCHAR (100) NOT NULL,
    description VARCHAR NOT NULL,
    img_url VARCHAR NOT NULL,
    icon VARCHAR (50) NOT NULL,
    CONSTRAINT PK_timeline PRIMARY KEY (id)
);

INSERT INTO notes (title, content)
VALUES ('Love at First Sight', 'The moment our eyes met, my world changed forever.');

INSERT INTO notes (title, content)
VALUES ('Forever & Always', 'No matter where life takes us, my heart will always find its way back to you.');

INSERT INTO notes (title, content)
VALUES ('Little Things', 'I love the way you smile, laugh, and make ordinary moments feel magical.');

INSERT INTO notes (title, content)
VALUES ('My Safe Place', 'In your arms, I find peace, comfort, and a love I never knew I needed.');

INSERT INTO notes (title, content)
VALUES ('Endless Love', 'Through every season of life, my love for you only grows stronger.');


COMMIT TRANSACTION;