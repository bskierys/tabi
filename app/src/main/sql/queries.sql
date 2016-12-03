-- wyszukiwanie po tablicy
SELECT _id, place_name, place_type, voivodeship, powiat, searched_plate, searched_plate_end FROM (

	SELECT * FROM (
		SELECT _id, MAX(plate_priority) AS max_plate_priority FROM plates_to_search WHERE searched_plate LIKE 'py%' GROUP BY _id
	) as o LEFT JOIN plates_to_search p ON o._id = p._id WHERE o.max_plate_priority = p.plate_priority
	
) as w WHERE searched_plate LIKE 'py%' ORDER BY length(searched_plate) ASC, place_type ASC,  searched_plate ASC,  searched_plate_end ASC;

-- wypisanie wszystkich kategorii na ekranie głównym
SELECT * FROM categories ORDER BY place_type ASC, voivodeship COLLATE localized ASC;

-- wyszukiwanie po nazwie miejsca
SELECT t._id as _id, t.place_name as place_name, t.place_type as place_type, t.voivodeship as voivodeship, t.powiat as powiat, t.plate as searched_plate,  t.plate_end as searched_plate_end FROM (

	SELECT *,  1 as grp FROM places_to_search WHERE place_name_to_lower LIKE "sam%"  
		UNION ALL  
	SELECT *,  2 as grp FROM places_to_search WHERE place_name_to_lower_no_diacritics LIKE "sam%" 
	
) AS t GROUP BY _id ORDER BY has_own_plate DESC, MIN(grp) ASC,  place_type ASC,  place_name;

-- wyszukiwanie w historii
SELECT * FROM search_history WHERE place_type = 1 GROUP BY place_id HAVING time_searched = MAX(time_searched);