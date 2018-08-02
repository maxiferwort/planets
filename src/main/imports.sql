CREATE TABLE days (
  day BIGINT NOT NULL,
  optimal_pression_and_temperature  BOOLEAN     NOT NULL,
  intensity_peak BOOLEAN     NOT NULL,
  weather VARCHAR(45) NOT NULL,
  perimeter DECIMAL NOT NULL DEFAULT 0,
  sun_in_the_middle BOOLEAN NOT NULL ,
  PRIMARY KEY (day)
);

CREATE TABLE planets (
  civilization VARCHAR(45) NOT NULL,
  initial_position DECIMAL NOT NULL DEFAULT 0,
  velocity BIGINT NOT NULL,
  sun_distance BIGINT NOT NULL,
  PRIMARY KEY (civilization)
);

INSERT INTO planets (civilization, initial_position, velocity, sun_distance) VALUES ('FERENGIES',0,1,500),('VULCANOS',0,-5,1000),('BETASOIDES',0,3,2000);