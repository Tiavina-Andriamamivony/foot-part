-- Insertion des clubs
INSERT INTO "Club" ("id", "name", "acronym", "yearCreation", "stadium", "coachName", "coachNationality") 
VALUES
  ('lyon', 'Olympique Lyonnais', 'OL', 1950, 'Groupama Stadium', 'Pierre Sage', 'Français'),
  ('marseille', 'Olympique de Marseille', 'OM', 1899, 'Orange Vélodrome', 'Jean-Louis Gasset', 'Français'),
  ('monaco', 'AS Monaco', 'ASM', 1924, 'Stade Louis-II', 'Adi Hütter', 'Autrichien'),
  ('lille', 'LOSC Lille', 'LOSC', 1944, 'Stade Pierre-Mauroy', 'Paulo Fonseca', 'Portugais');

-- Insertion des joueurs (20 joueurs)
INSERT INTO "Player" ("id", "name", "number", "position", "nationality", "age", "clubId")
VALUES
  -- Olympique Lyonnais (6 joueurs)
  ('lacazette', 'Alexandre Lacazette', 10, 'STRIKER', 'Français', 32, 'lyon'),
  ('caqueret', 'Maxence Caqueret', 6, 'MIDFIELDER', 'Français', 24, 'lyon'),
  ('lopes', 'Anthony Lopes', 1, 'GOAL_KEEPER', 'Portugais', 32, 'lyon'),
  ('gusto', 'Malo Gusto', 2, 'DEFENSE', 'Français', 20, 'lyon'),
  ('lukeba', 'Castello Lukeba', 4, 'DEFENSE', 'Français', 20, 'lyon'),
  ('tetê', 'Tetê', 7, 'STRIKER', 'Brésilien', 23, 'lyon'),

  -- Olympique de Marseille (6 joueurs)
  ('aubameyang', 'Pierre-Emerick Aubameyang', 9, 'STRIKER', 'Gabonais', 34, 'marseille'),
  ('clauss', 'Jonathan Clauss', 7, 'DEFENSE', 'Français', 31, 'marseille'),
  ('pau-lopez', 'Pau López', 16, 'GOAL_KEEPER', 'Espagnol', 28, 'marseille'),
  ('guendouzi', 'Matteo Guendouzi', 6, 'MIDFIELDER', 'Français', 24, 'marseille'),
  ('gigot', 'Samuel Gigot', 3, 'DEFENSE', 'Français', 30, 'marseille'),
  ('under', 'Cengiz Ünder', 17, 'STRIKER', 'Turc', 26, 'marseille'),

  -- AS Monaco (4 joueurs)
  ('ben-yedder', 'Wissam Ben Yedder', 10, 'STRIKER', 'Français', 33, 'monaco'),
  ('golovin', 'Aleksandr Golovin', 17, 'MIDFIELDER', 'Russe', 27, 'monaco'),
  ('vanderson', 'Vanderson', 2, 'DEFENSE', 'Brésilien', 22, 'monaco'),
  ('nübel', 'Alexander Nübel', 16, 'GOAL_KEEPER', 'Allemand', 26, 'monaco'),

  -- LOSC Lille (4 joueurs)
  ('david', 'Jonathan David', 9, 'STRIKER', 'Canadien', 23, 'lille'),
  ('cabella', 'Rémy Cabella', 10, 'MIDFIELDER', 'Français', 33, 'lille'),
  ('diakité', 'Bafodé Diakité', 4, 'DEFENSE', 'Français', 22, 'lille'),
  ('chevalier', 'Lucas Chevalier', 30, 'GOAL_KEEPER', 'Français', 21, 'lille'),

  -- Joueurs libres (sans club)
  ('benarfa', 'Hatem Ben Arfa', 18, 'MIDFIELDER', 'Français', 37, NULL),
  ('thauvin', 'Florian Thauvin', 28, 'STRIKER', 'Français', 30, NULL),
  ('sissoko', 'Moussa Sissoko', 17, 'MIDFIELDER', 'Français', 33, NULL);

-- Insertion de la saison
INSERT INTO "Season" ("id", "year", "alias", "status")
VALUES
  ('s2023', 2023, 'Ligue A 2023-2024', 'STARTED');

-- Insertion des matches
INSERT INTO "Match" ("id", "matchDatetime", "stadium", "status", "seasonId", "homeClubId", "awayClubId")
VALUES
  ('lyon-om', '2023-09-10 21:00:00', 'Groupama Stadium', 'FINISHED', 's2023', 'lyon', 'marseille'),
  ('monaco-lille', '2023-09-17 17:00:00', 'Stade Louis-II', 'STARTED', 's2023', 'monaco', 'lille');

-- Insertion des buts
INSERT INTO "Goal" ("id", "minuteOfGoal", "isOwnGoal", "matchId", "playerId", "clubId")
VALUES
  ('goal1', 28, false, 'lyon-om', 'lacazette', 'lyon'),
  ('goal2', 55, false, 'lyon-om', 'aubameyang', 'marseille'),
  ('goal3', 89, true, 'lyon-om', 'clauss', 'lyon'),
  ('goal4', 12, false, 'monaco-lille', 'benarfa', 'monaco');