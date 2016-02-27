-- wyszukiwanie po tablicy
select places._id, places.place_name, places.plate, places.plate_end, k.searched_plate, k.searched_plate_end from
	-- wybierz id miejsc o odpowiednich tablicach {
	(select m.ID as ID, m.plate as searched_plate, m.plate_end as searched_plate_end from
		-- pobiera wszystkie tablice zarówno podstawowe jak i dodatkowe w jednakowej formie jako tablicę łączoną {
		(select _id as ID, plate, plate_end from places where has_own_plate = 1 
		union 
		-- pobiera wszystkie dodatkowe tablice w miastach które mają własne tablice {
		select w._id as ID, w.plate_b as plate, w.plate_end as plate_end from 
		-- pobiera wszystkie dodatkowe tablice w odpowiedniej formie {
			(select p._id, a.plate as plate_b, a.plate_end as plate_end, p.has_own_plate from plates p join additional_plates a on p._id = a.place_id) 
			--}
		as w where w.has_own_plate = 1)
		--}		
		-- }
	as m where m.plate like "WW%" group by m.ID order by length(m.plate) asc, m.plate asc, m.plate_end asc) 
	-- }
as k left join places on k.ID = places._id;

-- wypisanie wszystkich kategorii na ekranie głównym
select places.voivodeship, places.plate, places.place_type from places where place_type = 0 or place_type = 5 group by places.voivodeship, places.place_type order by places.place_type asc, places.voivodeship collate localized asc;

-- wyszukiwanie po nazwie miejsca
SELECT _id, place_name, place_name_to_lower,search_phrase,place_type,voivodeship,powiat,gmina,plate,plate_end,has_own_plate,
       MIN(grp) AS source_group FROM

(SELECT *, 1 as grp FROM places where place_name_to_lower like "sw%" 
UNION ALL
SELECT *, 2 as grp FROM places where search_phrase like "sw%" ) as t

GROUP  BY _id
ORDER  BY MIN(grp) asc, place_type asc, place_name collate localized asc;

-- wyszukiwanie w historii
SELECT * FROM search_history WHERE place_type = 1 GROUP BY place_id HAVING time_searched = MAX(time_searched);