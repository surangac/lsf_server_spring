CREATE OR REPLACE TRIGGER mubasher_lsf.users_bir
		BEFORE INSERT ON users
		FOR EACH ROW
		BEGIN
			IF :NEW.id IS NULL THEN
				:NEW.id := users_seq.NEXTVAL;
			END IF;
	END;
/
