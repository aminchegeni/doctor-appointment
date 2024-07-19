INSERT INTO appointment (day, ref, start, status) VALUES (CURRENT_DATE, 'R0000000000000001', CURRENT_TIME, 'C');
INSERT INTO appointment (day, ref, start, status) VALUES (CURRENT_DATE, 'R0000000000000002', CURRENT_TIME + INTERVAL '30' MINUTE, 'C');
INSERT INTO appointment (day, ref, start, status) VALUES (CURRENT_DATE, 'R0000000000000003', CURRENT_TIME + interval '60' MINUTE, 'C');