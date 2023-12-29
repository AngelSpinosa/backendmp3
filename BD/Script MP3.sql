CREATE DATABASE MP3;
USE MP3;

CREATE TABLE Canciones (
	id INTEGER PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(255) NOT NULL,
    artista VARCHAR(255) NOT NULL,
    caratula VARCHAR(255),
    archivo_mp3 VARCHAR(255) NOT NULL
);

INSERT INTO Canciones (nombre, artista, caratula, archivo_mp3) 
VALUES 
    ('The Great Gig in the Sky', 'Pink Floyd', 'enlace_caratula_1', 'enlace_mp3_1'),
    ('Blue World', 'Mac Miller', 'enlace_caratula_2', 'enlace_mp3_2'),
    ('POWER', 'Kanye West', 'enlace_caratula_3', 'enlace_mp3_3');
    --Rutas provicionales hasta que conecte la API.