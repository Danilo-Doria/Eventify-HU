-- V3: Datos iniciales y masivos de Eventify
INSERT INTO categories (nombre, descripcion) VALUES
('Rock', 'Eventos relacionados con música rock'),
('Pop', 'Eventos relacionados con música pop'),
('Jazz', 'Eventos relacionados con música jazz'),
('Electrónica', 'Eventos de música electrónica'),
('Reggaeton', 'Eventos de música urbana y reggaeton'),
('Conferencia', 'Conferencias y charlas profesionales'),
('Tecnología', 'Eventos relacionados con tecnología'),
('Deportes', 'Eventos y actividades deportivas'),
('Arte', 'Exposiciones y actividades artísticas'),
('Teatro', 'Obras y presentaciones teatrales');

INSERT INTO venues (nombre, direccion, capacidad, ciudad) VALUES
('Centro de Convenciones', 'Calle 50 # 10-20', 500, 'Bogotá'),
('Movistar Arena', 'Carrera 30 # 57-60', 14000, 'Bogotá'),
('Teatro Colón', 'Calle 10 # 5-32', 900, 'Bogotá'),
('Auditorio Nacional', 'Carrera 7 # 40-50', 1200, 'Bogotá'),
('Centro Cultural del Caribe', 'Calle 36 # 46-66', 700, 'Barranquilla'),
('Estadio Metropolitano', 'Calle 45 # 1-85', 46000, 'Barranquilla'),
('Teatro Amira de la Rosa', 'Carrera 54 # 52-258', 600, 'Barranquilla'),
('Centro de Eventos del Caribe', 'Vía 40 # 79-06', 5000, 'Barranquilla'),
('Centro de Convenciones Cartagena', 'Calle 24 # 8A-344', 3000, 'Cartagena'),
('Teatro Heredia', 'Plaza de la Merced', 800, 'Cartagena'),
('Centro de Convenciones Valle del Pacífico', 'Calle 15 # 24-00', 2500, 'Cali'),
('Arena Cañaveralejo', 'Carrera 56 # 3-153', 18000, 'Cali'),
('Teatro Municipal de Cali', 'Carrera 5 # 6-64', 1200, 'Cali'),
('Centro de Convenciones Plaza Mayor', 'Calle 41 # 55-80', 5000, 'Medellín'),
('Teatro Metropolitano', 'Calle 41 # 57-30', 1634, 'Medellín'),
('Parque de los Deseos', 'Carrera 52 # 71-117', 3000, 'Medellín'),
('Centro Cultural Pereira', 'Carrera 7 # 26-08', 800, 'Pereira'),
('Auditorio del Eje Cafetero', 'Calle 19 # 9-50', 1000, 'Pereira'),
('Centro de Convenciones Bucaramanga', 'Carrera 33 # 52-10', 2000, 'Bucaramanga'),
('Teatro Santander', 'Carrera 19 # 31-65', 1000, 'Bucaramanga');

-- Garantiza que cada evento esté asociado a un Venue existente.
ALTER TABLE events
ADD CONSTRAINT fk_events_venue
FOREIGN KEY (venue_id)
REFERENCES venues(id);

-- Evento específico utilizado para validar la búsqueda
-- parcial e insensible a mayúsculas/minúsculas.
INSERT INTO events (nombre, fecha, descripcion, active, venue_id)
VALUES (
    'Concierto de ROCK',
    '2026-10-20 20:00:00',
    'Gran concierto de música rock.',
    TRUE,
    1
);

-- Genera 199 eventos adicionales para completar un catálogo
-- de al menos 200 registros variados.
INSERT INTO events (nombre, fecha, descripcion, active, venue_id)
SELECT
    CASE MOD(x, 10)
        WHEN 0 THEN 'Festival de Música'
        WHEN 1 THEN 'Conferencia de Tecnología'
        WHEN 2 THEN 'Torneo Deportivo'
        WHEN 3 THEN 'Exposición de Arte'
        WHEN 4 THEN 'Obra de Teatro'
        WHEN 5 THEN 'Concierto de Jazz'
        WHEN 6 THEN 'Festival Electrónico'
        WHEN 7 THEN 'Concierto de Pop'
        WHEN 8 THEN 'Evento de Negocios'
        ELSE 'Festival Cultural'
    END || ' #' || x,

    -- Genera fechas diferentes para cada evento.
    DATEADD('DAY', x, DATE '2026-10-01')
        + TIME '19:00:00',

    CASE MOD(x, 10)
        WHEN 0 THEN 'Festival musical con diferentes artistas.'
        WHEN 1 THEN 'Conferencia sobre innovación y tecnología.'
        WHEN 2 THEN 'Competencia deportiva para diferentes participantes.'
        WHEN 3 THEN 'Exposición con diferentes obras de arte.'
        WHEN 4 THEN 'Presentación teatral para público general.'
        WHEN 5 THEN 'Presentación de música jazz en vivo.'
        WHEN 6 THEN 'Festival de música electrónica.'
        WHEN 7 THEN 'Presentación de artistas de música pop.'
        WHEN 8 THEN 'Encuentro empresarial y profesional.'
        ELSE 'Actividad cultural para público general.'
    END,

    TRUE,

    -- Distribuye los eventos entre los 20 venues disponibles.
    MOD(x - 1, 20) + 1

FROM SYSTEM_RANGE(1, 199);

-- Asocia el evento especial "Concierto de ROCK" con la categoría Rock.
INSERT INTO events_categories (event_id, category_id)
VALUES (1, 1);

-- Asigna una categoría a cada uno de los eventos restantes.
INSERT INTO events_categories (event_id, category_id)
SELECT
    x,
    MOD(x - 1, 10) + 1
FROM SYSTEM_RANGE(2, 200);

-- Agrega una segunda categoría a parte de los eventos.
INSERT INTO events_categories (event_id, category_id)
SELECT
    x,
    MOD(x + 2, 10) + 1
FROM SYSTEM_RANGE(2, 100);
