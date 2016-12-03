-- wyszukiwanie po tablicy
SELECT * FROM plates_to_search WHERE searched_plate LIKE 'd%' GROUP BY _id  ORDER BY length(searched_plate) ASC, place_type ASC,  searched_plate ASC,  searched_plate_end ASC;

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