-- =================== wyszukiwanie po tablicy =========================
SELECT _id, place_name, place_type, voivodeship, powiat, searched_plate, searched_plate_end FROM (

	SELECT * FROM (
		SELECT _id, MAX(plate_priority) AS max_plate_priority FROM plates_to_search WHERE searched_plate LIKE 'py%' GROUP BY _id
	) as o LEFT JOIN plates_to_search p ON o._id = p._id WHERE o.max_plate_priority = p.plate_priority
	
) as w WHERE searched_plate LIKE 'py%' ORDER BY place_type ASC, length(searched_plate) ASC, searched_plate ASC,  searched_plate_end ASC;

-- =================== wyszukiwanie po nazwie miejsca ===================
SELECT t._id as _id, t.place_name as place_name, t.place_type as place_type, t.voivodeship as voivodeship, t.powiat as powiat, t.plate as searched_plate,  t.plate_end as searched_plate_end FROM (

	SELECT *,  1 as grp FROM places_to_search WHERE place_name_to_lower LIKE "sam%"  
		UNION ALL  
	SELECT *,  2 as grp FROM places_to_search WHERE place_name_to_lower_no_diacritics LIKE "sam%" 
	
) AS t GROUP BY _id ORDER BY has_own_plate DESC, MIN(grp) ASC,  place_type ASC,  place_name COLLATE LOCALIZED ASC;

-- =================== wypisanie wszystkich kategorii na ekranie głównym =======
SELECT * FROM categories ORDER BY place_type ASC, voivodeship COLLATE localized ASC;

-- =================== wyszukiwanie w historii =========================
SELECT f1._id as _id,f1.place_name as place_name, f1.place_type as place_type, f1.voivodeship as voivodeship, f1.powiat as powiat, 
f1.plate as searched_plate, f1.plate_end as searched_plate_end FROM (
 
	SELECT p._id as _id,p.place_name as place_name, p.place_type as place_type,p.voivodeship as voivodeship, p.powiat as powiat, s.plate as plate,null as plate_end FROM 
			search_history s left join places p on s.place_id = p._id WHERE s.search_type = 0 ORDER BY s.time_searched DESC LIMIT 2
		
) AS f1  
-- dołącz losową tablicę
UNION ALL 

SELECT f2._id as _id,f2.place_name as place_name, f2.place_type as place_type,f2.voivodeship as voivodeship,f2.powiat as powiat,
f2.plate as searched_plate,f2.plate_end as searched_plate_end FROM (
	-- dla miejsc
	SELECT _id,place_name,6 as place_type,voivodeship,powiat,plate,plate_end
	FROM places WHERE place_type < 5 LIMIT 1 OFFSET ABS(RANDOM() % 44045)
	
	-- lub dla tablic
	--SELECT _id,place_name,6 as place_type,voivodeship,powiat,plate,plate_end
	--FROM places WHERE place_type < 5 AND has_own_plate = 1 LIMIT 1 OFFSET ABS(RANDOM() % 397)
	
) as f2

-- ilość miejsc
SELECT COUNT(*) FROM places WHERE place_type < 5;
-- ilość tablic
SELECT COUNT(*) FROM places WHERE place_type < 5 AND has_own_plate = 1