
-- CreateEnum
CREATE TYPE "PlayerPosition" AS ENUM ('STRIKER', 'MIDFIELDER', 'DEFENSE', 'GOAL_KEEPER');

-- CreateEnum
CREATE TYPE "SeasonStatus" AS ENUM ('NOT_STARTED', 'STARTED', 'FINISHED');

-- CreateEnum
CREATE TYPE "MatchStatus" AS ENUM ('NOT_STARTED', 'STARTED', 'FINISHED');

-- CreateTable
CREATE TABLE "Club" (
                        "id" TEXT NOT NULL,
                        "name" TEXT NOT NULL,
                        "acronym" TEXT NOT NULL,
                        "yearCreation" INTEGER,
                        "stadium" TEXT,
                        "coachName" TEXT,
                        "coachNationality" TEXT,

                        CONSTRAINT "Club_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "Player" (
                          "id" TEXT NOT NULL,
                          "name" TEXT NOT NULL,
                          "number" INTEGER NOT NULL,
                          "position" "PlayerPosition" NOT NULL,
                          "nationality" TEXT NOT NULL,
                          "age" INTEGER NOT NULL,
                          "clubId" TEXT,

                          CONSTRAINT "Player_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "Season" (
                          "id" TEXT NOT NULL,
                          "year" INTEGER NOT NULL,
                          "alias" TEXT NOT NULL,
                          "status" "SeasonStatus" NOT NULL DEFAULT 'NOT_STARTED',

                          CONSTRAINT "Season_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "Match" (
                         "id" TEXT NOT NULL,
                         "matchDatetime" TIMESTAMP(3) NOT NULL,
                         "stadium" TEXT NOT NULL,
                         "status" "MatchStatus" NOT NULL DEFAULT 'NOT_STARTED',
                         "seasonId" TEXT NOT NULL,
                         "homeClubId" TEXT NOT NULL,
                         "awayClubId" TEXT NOT NULL,

                         CONSTRAINT "Match_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "Goal" (
                        "id" TEXT NOT NULL,
                        "minuteOfGoal" INTEGER NOT NULL,
                        "isOwnGoal" BOOLEAN NOT NULL DEFAULT false,
                        "matchId" TEXT NOT NULL,
                        "playerId" TEXT NOT NULL,
                        "clubId" TEXT NOT NULL,

                        CONSTRAINT "Goal_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "Transfer" (
    "id" TEXT NOT NULL,
    "playerId" TEXT NOT NULL,
    "clubId" TEXT,
    "transferDate" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "Transfer_pkey" PRIMARY KEY ("id")
);

-- CreateIndex
CREATE INDEX "Transfer_playerId_idx" ON "Transfer"("playerId");
CREATE INDEX "Transfer_clubId_idx" ON "Transfer"("clubId");
CREATE INDEX "Transfer_transferDate_idx" ON "Transfer"("transferDate");

-- AddForeignKey
ALTER TABLE "Transfer" ADD CONSTRAINT "Transfer_playerId_fkey" FOREIGN KEY ("playerId") REFERENCES "Player"("id") ON DELETE RESTRICT ON UPDATE CASCADE;
ALTER TABLE "Transfer" ADD CONSTRAINT "Transfer_clubId_fkey" FOREIGN KEY ("clubId") REFERENCES "Club"("id") ON DELETE SET NULL ON UPDATE CASCADE;

-- CreateIndex
CREATE UNIQUE INDEX "Club_name_key" ON "Club"("name");

-- CreateIndex
CREATE INDEX "Club_name_idx" ON "Club"("name");

-- CreateIndex
CREATE INDEX "Player_name_idx" ON "Player"("name");

-- CreateIndex
CREATE INDEX "Player_clubId_idx" ON "Player"("clubId");

-- CreateIndex
CREATE INDEX "Player_age_idx" ON "Player"("age");

-- CreateIndex
CREATE UNIQUE INDEX "Season_year_key" ON "Season"("year");

-- CreateIndex
CREATE INDEX "Season_year_idx" ON "Season"("year");

-- CreateIndex
CREATE INDEX "Season_status_idx" ON "Season"("status");

-- CreateIndex
CREATE INDEX "Match_seasonId_idx" ON "Match"("seasonId");

-- CreateIndex
CREATE INDEX "Match_homeClubId_idx" ON "Match"("homeClubId");

-- CreateIndex
CREATE INDEX "Match_awayClubId_idx" ON "Match"("awayClubId");

-- CreateIndex
CREATE INDEX "Match_status_idx" ON "Match"("status");

-- CreateIndex
CREATE INDEX "Match_matchDatetime_idx" ON "Match"("matchDatetime");

-- CreateIndex
CREATE INDEX "Goal_matchId_idx" ON "Goal"("matchId");

-- CreateIndex
CREATE INDEX "Goal_playerId_idx" ON "Goal"("playerId");

-- CreateIndex
CREATE INDEX "Goal_clubId_idx" ON "Goal"("clubId");

-- AddForeignKey
ALTER TABLE "Player" ADD CONSTRAINT "Player_clubId_fkey" FOREIGN KEY ("clubId") REFERENCES "Club"("id") ON DELETE SET NULL ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "Match" ADD CONSTRAINT "Match_seasonId_fkey" FOREIGN KEY ("seasonId") REFERENCES "Season"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "Match" ADD CONSTRAINT "Match_homeClubId_fkey" FOREIGN KEY ("homeClubId") REFERENCES "Club"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "Match" ADD CONSTRAINT "Match_awayClubId_fkey" FOREIGN KEY ("awayClubId") REFERENCES "Club"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "Goal" ADD CONSTRAINT "Goal_matchId_fkey" FOREIGN KEY ("matchId") REFERENCES "Match"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "Goal" ADD CONSTRAINT "Goal_playerId_fkey" FOREIGN KEY ("playerId") REFERENCES "Player"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "Goal" ADD CONSTRAINT "Goal_clubId_fkey" FOREIGN KEY ("clubId") REFERENCES "Club"("id") ON DELETE RESTRICT ON UPDATE CASCADE;
