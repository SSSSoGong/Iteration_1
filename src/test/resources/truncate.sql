SET
REFERENTIAL_INTEGRITY FALSE;
TRUNCATE TABLE ADMIN RESTART IDENTITY;
TRUNCATE TABLE "'user'" RESTART IDENTITY;
/*TRUNCATE TABLE ROLE RESTART IDENTITY;*/
TRUNCATE TABLE PROJECT RESTART IDENTITY;
TRUNCATE TABLE USER_PROJECT RESTART IDENTITY;
TRUNCATE TABLE ISSUE RESTART IDENTITY;
TRUNCATE TABLE COMMENT RESTART IDENTITY;
TRUNCATE TABLE ISSUE_MODIFICATION RESTART IDENTITY;
SET
REFERENTIAL_INTEGRITY TRUE;
