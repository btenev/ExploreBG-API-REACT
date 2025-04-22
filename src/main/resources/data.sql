INSERT INTO roles (id, role)
SELECT 1, 'MEMBER'
FROM DUAL
WHERE NOT EXISTS (SELECT * FROM roles WHERE id = 1);

INSERT INTO roles (id, role)
SELECT 2, 'MODERATOR'
FROM DUAL
WHERE NOT EXISTS (SELECT * FROM roles WHERE id = 2);

INSERT INTO roles (id, role)
SELECT 3, 'ADMIN'
FROM DUAL
WHERE NOT EXISTS (SELECT * FROM roles WHERE id = 3);

INSERT INTO images (id, cloud_id, folder, image_url, status, creation_date)
VALUES (1, '707325df-b536-47e2-b511-d106296ad5ec', 'Users-demo',
        'https://res.cloudinary.com/dcljilaws/image/upload/v1725364534/Users-demo/707325df-b536-47e2-b511-d106296ad5ec',
        'APPROVED', UTC_TIMESTAMP()),
       (2, '98196dc3-825e-467f-b98f-3f433b731d0d', 'Users-demo',
        'https://res.cloudinary.com/dcljilaws/image/upload/v1725368531/Users-demo/98196dc3-825e-467f-b98f-3f433b731d0d',
        'APPROVED', UTC_TIMESTAMP()),
       (3, '2e121ec2-3708-4555-a709-c92f79f6aac0', 'Users-demo',
        'https://res.cloudinary.com/dcljilaws/image/upload/v1725369492/Users-demo/2e121ec2-3708-4555-a709-c92f79f6aac0',
        'APPROVED', UTC_TIMESTAMP()),
       (4, 'dfe9b4a6-4124-46e5-b560-5b8a6068a538', 'Trails-demo',
        'https://res.cloudinary.com/dcljilaws/image/upload/v1726746408/Trails-demo/dfe9b4a6-4124-46e5-b560-5b8a6068a538',
        'APPROVED', UTC_TIMESTAMP()),
       (5, 'e8f6e009-dcd9-42dd-9219-d56cc0c7b2ca', 'Trails-demo',
        'https://res.cloudinary.com/dcljilaws/image/upload/v1726746409/Trails-demo/e8f6e009-dcd9-42dd-9219-d56cc0c7b2ca',
        'APPROVED', UTC_TIMESTAMP()),
       (6, '78939ad4-50bf-4d3d-bf08-63e05440a3bb', 'Trails-demo',
        'https://res.cloudinary.com/dcljilaws/image/upload/v1726746411/Trails-demo/78939ad4-50bf-4d3d-bf08-63e05440a3bb',
        'APPROVED', UTC_TIMESTAMP()),
       (7, '2b073089-aaa4-4b62-bd83-478d62b3e0b3', 'Trails-demo',
        'https://res.cloudinary.com/dcljilaws/image/upload/v1726746413/Trails-demo/2b073089-aaa4-4b62-bd83-478d62b3e0b3',
        'APPROVED', UTC_TIMESTAMP()),
       (8, '61429b5d-3a33-459e-b936-a8cd3b91df82', 'Trails-demo',
        'https://res.cloudinary.com/dcljilaws/image/upload/v1726746415/Trails-demo/61429b5d-3a33-459e-b936-a8cd3b91df82',
        'APPROVED', UTC_TIMESTAMP()),
       (9, '88118c19-eb95-48ae-8ffc-0656f85f2961', 'Trails-demo',
        'https://res.cloudinary.com/dcljilaws/image/upload/v1726746417/Trails-demo/88118c19-eb95-48ae-8ffc-0656f85f2961',
        'APPROVED', UTC_TIMESTAMP()),
       (10, '4386094c-44e8-4de2-868e-bcb0a08c9a91', 'Trails-demo',
        'https://res.cloudinary.com/dcljilaws/image/upload/v1726746419/Trails-demo/4386094c-44e8-4de2-868e-bcb0a08c9a91',
        'APPROVED', UTC_TIMESTAMP()),
       (11, '194322e9-dbaf-49bb-aaf2-f0d178125c81', 'Trails-demo',
        'https://res.cloudinary.com/dcljilaws/image/upload/v1726746421/Trails-demo/194322e9-dbaf-49bb-aaf2-f0d178125c81',
        'APPROVED', UTC_TIMESTAMP()),
       (12, 'dc3a1eb7-23ed-4099-9ace-2ff3e00c72a4', 'Trails-demo',
        'https://res.cloudinary.com/dcljilaws/image/upload/v1726746423/Trails-demo/dc3a1eb7-23ed-4099-9ace-2ff3e00c72a4',
        'APPROVED', UTC_TIMESTAMP()),
       (13, 'a406254e-4966-4c82-b18a-109dc8209539', 'Trails-demo',
        'https://res.cloudinary.com/dcljilaws/image/upload/v1726746425/Trails-demo/a406254e-4966-4c82-b18a-109dc8209539',
        'APPROVED', UTC_TIMESTAMP()),
       (14, '7f2eb04f-96eb-48d9-b74a-32ac0a341552', 'Trails-demo',
        'https://res.cloudinary.com/dcljilaws/image/upload/v1727259806/Trails-demo/7f2eb04f-96eb-48d9-b74a-32ac0a341552',
        'PENDING', UTC_TIMESTAMP()),
       (15, 'fab873e4-d3d0-4dfa-bd43-5dbaeee68bc8', 'Trails-demo',
        'https://res.cloudinary.com/dcljilaws/image/upload/v1727259809/Trails-demo/fab873e4-d3d0-4dfa-bd43-5dbaeee68bc8',
        'PENDING', UTC_TIMESTAMP()),
       (16, 'c11855bf-53f0-4774-970a-07e89e21c2fc', 'Destinations-demo',
        'https://res.cloudinary.com/dcljilaws/image/upload/v1735992975/Destinations-demo/c11855bf-53f0-4774-970a-07e89e21c2fc',
        'APPROVED', UTC_TIMESTAMP()),
       (17, '6a4643a2-ef0f-4e80-a866-4a1f6a21a00a', 'Destinations-demo',
        'https://res.cloudinary.com/dcljilaws/image/upload/v1735993812/Destinations-demo/6a4643a2-ef0f-4e80-a866-4a1f6a21a00a',
        'APPROVED', UTC_TIMESTAMP()),
       (18, '08cc663b-0db8-4304-af3a-7f451729385d', 'Destinations-demo',
        'https://res.cloudinary.com/dcljilaws/image/upload/v1735994381/Destinations-demo/08cc663b-0db8-4304-af3a-7f451729385d',
        'APPROVED', UTC_TIMESTAMP()),
       (19, '404e320a-0b16-4314-b034-20e833583bb0', 'Destinations-demo',
        'https://res.cloudinary.com/dcljilaws/image/upload/v1735997440/Destinations-demo/404e320a-0b16-4314-b034-20e833583bb0',
        'APPROVED', UTC_TIMESTAMP()),
       (20, '3d567927-47d9-4911-9936-673ef14cd2a1', 'Destinations-demo',
        'https://res.cloudinary.com/dcljilaws/image/upload/v1735997780/Destinations-demo/3d567927-47d9-4911-9936-673ef14cd2a1',
        'APPROVED', UTC_TIMESTAMP()),
       (21, 'a9c3836c-2b31-4ca2-844f-03e577da093b', 'Destinations-demo',
        'https://res.cloudinary.com/dcljilaws/image/upload/v1735998334/Destinations-demo/a9c3836c-2b31-4ca2-844f-03e577da093b',
        'APPROVED', UTC_TIMESTAMP()),
       (22, '0d3c6068-5204-4122-9d9b-5a2fc87afede', 'Destinations-demo',
        'https://res.cloudinary.com/dcljilaws/image/upload/v1735998597/Destinations-demo/0d3c6068-5204-4122-9d9b-5a2fc87afede',
        'APPROVED', UTC_TIMESTAMP());

INSERT INTO users (id, email, username, password, gender, birthdate, user_info, creation_date, account_non_locked,
                   user_image_id)
VALUES (1, 'member@explore.bg', 'member', '$2a$10$HG8ZKzRo7XTAvOZGa9Kx/e.RIGyr3HufztnjuwBOaVSdlvGnUFWuG', 'MALE',
        '1983-05-10',
        'some very interesting info', UTC_TIMESTAMP(), true, 2),
       (2, 'moderator@explore.bg', 'moderator', '$2a$10$HG8ZKzRo7XTAvOZGa9Kx/e.RIGyr3HufztnjuwBOaVSdlvGnUFWuG',
        'FEMALE',
        '1979-02-01',
        'not so interesting info', UTC_TIMESTAMP(), true, 3),
       (3, 'member_two@explore.bg', 'memberTwo', '$2a$10$HG8ZKzRo7XTAvOZGa9Kx/e.RIGyr3HufztnjuwBOaVSdlvGnUFWuG', 'MALE',
        '1965-03-08',
        'i like like travelling, hiking, exploring the world', UTC_TIMESTAMP(), true, null),
       (4, 'admin@explore.bg', 'admin', '$2a$10$HG8ZKzRo7XTAvOZGa9Kx/e.RIGyr3HufztnjuwBOaVSdlvGnUFWuG', 'MALE',
        '1983-05-15', 'I like watching discovery chanel', UTC_TIMESTAMP(), true, 1);

INSERT INTO user_role (role_id, user_id)
VALUES (1, 1),
       (1, 2),
       (2, 2),
       (1, 3),
       (1, 4),
       (3, 4);

INSERT INTO accommodations (price_per_bad, bed_capacity, food_available, id, created_by_id, accommodation_info,
                            accommodation_name, next_to, phone_number,
                            main_image_id, site, access, type, accommodation_status, status, max_number_of_images,
                            creation_date)
VALUES (null, null, 1, 1, 3, 'A place where you can have some rest and get some food', 'hija Akademic', 'Zheleznitsa',
        null,
        null, null, 'BY_CAR', 'HUT', 'APPROVED', 'APPROVED', 10, UTC_TIMESTAMP()),
       (null, null, 1, 2, 2, 'A place where you can have some rest and get some food', 'zaslon Cherni Vrah',
        'Zheleznitsa',
        '+35985738923', null, null, 'BY_CAR', 'SHELTER', 'APPROVED', 'APPROVED', 10, UTC_TIMESTAMP()),
       (null, 25, 1, 3, 2, 'A place where you can have some rest and get some food', 'hija Perelik', 'Smolyan', null,
        null, null, 'BY_CAR', 'HUT', 'APPROVED', 'APPROVED', 10, UTC_TIMESTAMP()),
       (null, 30, 1, 4, 1, 'A place where you can have some rest and get some food', 'hija Ravnets', 'Karlovo',
        '+359 896 68 85 23', null, 'https://www.btsbg.org/hizhi/hizha-ravnec',
        'ON_FOOT', 'HUT', 'APPROVED', 'APPROVED', 10, UTC_TIMESTAMP()),
       (null, 35, 1, 5, 1, 'A place where you can have some rest and get some food', 'Zaslon Botev', 'Karlovo, Kalofer',
        '0878 197451', null, 'https://www.btsbg.org/hizhi/zaslon-botev', 'BY_CAR', 'SHELTER',
        'APPROVED', 'APPROVED', 10, UTC_TIMESTAMP()),
       (25, 22, 1, 6, 1, 'A place where you can have some rest and get some food', 'Turisticheska spalnia Botev',
        'Karlovo, Kalofer', '0877 273 547', null, 'https://botevbg.com/', 'BY_CAR', 'SHELTER',
        'PENDING', 'PENDING', 10, UTC_TIMESTAMP());

INSERT INTO destinations(id, destination_info, main_image_id, location, destination_name, next_to, type,
                         destination_status,
                         status, creation_date, max_number_of_images, created_by_id)
VALUES (1,
        'Mezeshka Fortress (Neuzetikon or Neutzikon[1]) is a medieval Byzantine fortification, built in the 11th - 12th centuries west of today''s Mezek village, Svilengrad municipality. It is located on an elongated terrace, formed at the foot of a steep branch of the northeastern Rhodopes, bearing the name St. Marina or the Forest. The local population calls the fortress "Kaleto". The fortress had the functions of a border guard fortress. It guarded territories between the Maritsa and Arda rivers.',
        16,
        'coordinates', 'Mezeshka Krepost', 'Svilengrad', 'CULTURAL_HERITAGE', 'APPROVED', 'APPROVED', UTC_TIMESTAMP(),
        10, 1),
       (2,
        'The Meses tomb is a Thracian dome tomb of the Mycenaean type dated to the 4th – 3rd centuries BC. It is located on the outskirts of Mezek village, Svilengrad municipality, in the "Meltepe" mound. This is one of the largest Thracian tombs in Bulgaria.',
        17,
        'coordinates', 'Mezeshka grobnica', 'Svilengrad', 'CULTURAL_HERITAGE', 'PENDING', 'PENDING', UTC_TIMESTAMP(),
        10, 1),
       (3,
        'The village of Matochina is located next to the Bulgarian-Turkish border, on the right bank of the Tundzha River. At its northern end rises a steep hill accessible only from the village. There was a partition wall here, of which only traces remain now. 50 m from it, up the hill, was the Bukelon fortress - the closest guard of Edirne from the north. It is one of the best preserved military buildings in our country.',
        18, 'coordinates',
        'Kreposta Bukelon',
        'Svilengrad', 'CULTURAL_HERITAGE', 'APPROVED', 'APPROVED', UTC_TIMESTAMP(), 10, 1),
       (4,
        'The Visegrad fortress is located on a high peak, near Kardzhali. It is believed to date from the 4th - 1st centuries. Its area is relatively small - 562 square meters, and according to its structure and dimensions, it represents a medieval feudal castle. It guarded a branch of the road from Adrianople to Philippopolis to the interior of the Rhodopes along the valley of the river Varbitsa. The name of the fortress is of Slavic origin and means "high city". The fortress is easily accessible and convenient to visit. It is reached by a relatively good dirt road.',
        19, 'coordinates',
        'Krepost Vishegrad', 'Kardzhali',
        'CULTURAL_HERITAGE', 'APPROVED', 'APPROVED', UTC_TIMESTAMP(), 10, 1),
       (5,
        'The Kardzhali pyramids are rock formations located near the town of Kardzhali, in the Rhodopes. They were declared a natural landmark in 1974. They were formed more than 40 million years ago. The most interesting group of rocks are the Stone Wedding. It contains pyramids that resemble people and animals. Another interesting group is the Stone Mushrooms.',
        20,
        'coordinates', 'Kardzhaliiski piramidi', 'Kardzhali', 'NATURAL_ATTRACTION', 'APPROVED', 'APPROVED',
        UTC_TIMESTAMP(), 10, 2),
       (6,
        'The Letovnik waterfall is located next to the village of Letovnik, Momchilgrad. It is not tall or powerful, but the rocks from which it falls and the area around it are uniquely beautiful.',
        21, 'coordinates',
        'Letovnishki vodopad', 'Momchilgard',
        'NATURAL_ATTRACTION', 'APPROVED', 'APPROVED', UTC_TIMESTAMP(), 10, 2),
       (7,
        'Perperikon is an archaeological complex located 15 km from the town of Kardzhali. It was inhabited as early as 8,000 years ago, during the Stone Age, after which it was a sacred rock city under the Thracians, and later Romans, Byzantines and Bulgarians lived there. It was destroyed by the Turks in the 14th century.',
        22, 'coordinates',
        'Perperikon', 'Kardzhali', 'CULTURAL_HERITAGE', 'APPROVED', 'APPROVED', UTC_TIMESTAMP(), 10, 2),
       (8, 'Some random destination', null, 'coordinates', 'Nowhere', 'Unknown', 'CULTURAL_HERITAGE', 'APPROVED',
        'APPROVED', UTC_TIMESTAMP(), 10, null);

INSERT INTO hiking_trails (id, start_point, end_point, total_distance, trail_info, season_visited,
                           water_available, trail_difficulty, elevation_gained, next_to, creation_date, trail_status,
                           max_number_of_images, created_by_id, main_image_id, status)
VALUES (1, 'Selo Zheleznitsa', 'Cherni vrah', 9.00,
        'The route from the village of Zheleznitsa to Cherni vrah (2290 m) is interesting, somewhat varied and above all beautiful. Some say it is the most picturesque ascent of the summit of Vitosha. So far I think so, but I haven''t climbed it on the southern routes yet. Of course, before you go, you should keep in mind that this hike overcomes nearly 1,300 meters of positive elevation gain. Consider your physical strength well and keep in mind that you have to get down somehow, the closest point after that being the Aleko hut.',
        'SUMMER', 'NO_INFORMATION', 4, 1258, 'Zheleznitsa', UTC_TIMESTAMP(),
        'APPROVED', 10, 4, 4, 'APPROVED'),
       (2, 'Selo Planinovo', 'Vrah Vishegrad', 9.00, 'A nice walk in Sakar mountain', 'WINTER', 'NO', 2, 405,
        'Selo Planinovo', UTC_TIMESTAMP(), 'APPROVED', 10, 1, null, 'APPROVED'),
       (3, 'hija Perelik', 'vrah Orfei', 7.5,
        'Mount Orpheus has many names, and you may have heard it by some of them - Broad-eared Snowflake, Spiked Hammer, Snowflake, Snowflake, Big Snowflake (there are probably more). It is the second highest peak in the Rhodopes after Golyam Perelik and is only 3 m lower than it. Since Perelik is a war zone and inaccessible, we can only go past it and go to its runner-up. The route follows the red tourist markings and intersects the peaks Shabalieva Kaba (2087 m) and Krastava Chuka (2136 m), which we can visit with a slight detour. The elevation graph looks broken simply because it''s a little over 200 meters. Nothing too complicated.
         We leave the Perelik hut on the main road in the direction of Golyam Perelik peak. At Branch 2, the marking leaves it and a little later the ascent begins. Here are also the first spikes from the winter tourist marking, which are rare in some places, but they are with us up to Mount Orpheus. The route undercuts the highest peak of the Rhodopes and we continue along the ridge. We can define the transition as almost horizontal. Sometimes we go up, sometimes we go down, but the differences in elevation are small.
         Between the peaks of Shabalieva Kaba and Krastava Chuka, a blue marking goes down, which goes down the Gingerska Reka to the main road between the town of Smolyan and the village of Mugla. However, we continue on the red road. We cut down the Hammer Toad and head to the cherished point of our route, which is getting closer. The path itself intersects it from the south, but we deviate from it in the direction of the goal.
         After about 300 m we reach Mount Orpheus. From it you can see the entire Rhodope Mountains and the surrounding area - the best end of our trek. Here we can see that Orpheus is written on the official inscription at the top.',
        'WINTER', 'NO', 2, 223, 'Smolyan', UTC_TIMESTAMP(), 'APPROVED', 10, 2, null, 'APPROVED'),
       (4, 'Gara Skakavica', 'Vodopad Skakavica', 1.75, 'Polska Skakavitsa waterfall is one of the most beautiful waterfalls in Bulgaria! Too bad it''s not full year round. It is best to visit it in the spring, perhaps after nature has blossomed, to see it in its full glory.
        The waterfall is easily accessible from the village of Polska Skakavitsa, but there is another route to it that is super interesting. I will describe it, and the starting point can only be reached by train. I especially recommend this hike to the Polska Skakavitsa waterfall with children, because for them it offers an unforgettable adventure - a train ride, a railway trek, passing through a tunnel, walking on forest paths, crossing streams, and a fabulous experience in the rock niches behind the waterfall.',
        'SUMMER', 'NO', 1, 55, 'Kustendil', UTC_TIMESTAMP(), 'APPROVED', 10, 3, null, 'APPROVED'),
       (5, 'Grad Devin', 'Rezervat Kastrakli', 30, 'Polska Skakavitsa waterfall is one of the most beautiful waterfalls in Bulgaria! Too bad it''s not full year round. It is best to visit it in the spring, perhaps after nature has blossomed, to see it in its full glory.
        The waterfall is easily accessible from the village of Polska Skakavitsa, but there is another route to it that is super interesting. I will describe it, and the starting point can only be reached by train. I especially recommend this hike to the Polska Skakavitsa waterfall with children, because for them it offers an unforgettable adventure - a train ride, a railway trek, passing through a tunnel, walking on forest paths, crossing streams, and a fabulous experience in the rock niches behind the waterfall.
        The eco-trail ends in the Lakata area, where there is a turnoff for the Kavour Kale fortress. It diverts us from the road about 800 m in one direction. Not much remains of the fortress itself, but the view from the top is stunning. Two waterfalls can be seen, one of which is about 50 m high and the other consists of five jumps.
        After the fortress, we go without a path along the left bank of the Devinska river. In this direction, we reach the confluence of the Devinska and Sachandere rivers. From here we ascend Mount Karaburun, which turns out to be an astonishingly beautiful place, surrounded by formidable precipices to the south and east. We continue along the ridge on a dirt road and reach almost the source of the Karaburunska River. We catch its course without a path and continue like this until we reach the place where it flows into Devinska.
        We start moving along it and after several crossings, now on one bank, then on the other, we reach the Kemerovo Bridge, where it joins the Katranjidere River. Devinska is on the right, and we pass the other on a nice road. Before long there is another fork in the river bed and we head off on the left tributary, south. We pass a fork where we keep straight and after a few hundred meters we reach a fountain. Shortly after it, a new branch follows, whose eastern branch we take. He takes us to a nice dirt road.
        We only follow him, who accompanies us all the way back. At the junctions, we avoid right turns, except for the one marked as Junction 4. We pass through the Kastrakli reserve and, crossing the ridge of the Devin massifs, make the transition back to Devin.',
        'SUMMER', 'YES', 3, 667, 'Devin', UTC_TIMESTAMP(), 'APPROVED', 10, 1, null, 'APPROVED'),
       (6, 'Grad Zemen', 'Vrah Tichak', 10, 'This route is the easiest for the ascent of the peak of Zemenska Planina - Tichak peak, although it is 10 km. Long. This is mainly due to the road that accompanies us all the way - from the bottom to the very top. With normal preparation, it shouldn''t take more than two hours. It should be noted that Tichak itself is not accessible, as it is located in a war zone, but we get relatively close to it. There is no tourist marking, but since it is impossible to get lost, we have recorded 4 marking points.,
        We start from the entrance of the limestone quarry, located in the northern part of the city, which is passed under the bridge on the road to the Zemen Monastery. Initially, the pavement was paved. It goes on like this for a while, then switches to concrete. The road winds upwards and slowly gains elevation. From the first two turns there is a magnificent view of Zemen and the hills in the distance. Then, for several kilometers, we walk in the open. The road turns into asphalt and gradually enters the forest.
        So we walk for about 2 km and come out into the open again, with Tichak towering in front of us. We continue to walk up the road, leaving it for a bit at one point, just for a change and to feel that we have a mountain under our feet. We pass by a monument to fallen partisans, then we go down to the road again.
        So we go to the top, where we are met by the big barrier of the division. Be careful not to startle the German shepherds guarding the yard when you realize there is a hole in the fence and they can get out. At least handsome Rex was untied and out, but we had no trouble with him, either because the soldier was with us, or because the dog himself was good.
        Anyway, our route ends here. We see the peak nearby, on which there is a pile of military equipment, then we make our way down. If we don''t want to go to Zemen, we can go down to the village of Dobri dol by one of the two routes - "route 1" and the more circuitous, but pleasant "route 2".',
        'SUMMER', 'NO', 2, 690, 'Pernik', UTC_TIMESTAMP(), 'PENDING', 10, 2, null, 'PENDING'),
       (7, 'Grad Karlovo', 'Vrah Botev', 17.25, 'The route to Mount Botev (2376 m) via the Ravnets hut is one of the two options for climbing from Karlovo. It respects with its long initial ascent from the foothills to the ridge of Stara planina. For the first 7 kilometers (about 4:30 hours), 1400 meters of elevation gain are overcome. The trek then starts on almost flat terrain for nearly 3 hours, before the final ascent of Mount Botev.
        Such load distribution is somewhat good. Thus, most of the climb is overcome with the freshest strength, followed by a long horizontal walk for a "rest" before the steep finish. In addition, leaving early avoids the heat on the hardest section.
        In general, the sections Karlovo - Ravnets hut and Ravnets hut - Botev peak are 2:30 and 5:45 respectively. Given that combined they form a very long transition and serious fatigue accumulates, I add half an hour to the total time.
        We are heading from Karlovo to Mount Botev, and the first stop is the Ravnets hut. The starting point is the Besh Bunar area, at the northeastern end of the city, and right next to a rest station called the Russian Villa. It can be reached by car, but you have to park somewhere in the meadows. If you are coming on foot - it is located 500 meters from the pedestrian zone by the park.
        After five minutes of walking, we come to a fork in the river valley. Here we make a choice whether to reach Ravnets hut by the red or blue markings. I will say a few words about both. They are the same as time. This GPS track is recorded on the blue.
        Blue – On the other side is the blue route to the Ravnets hut, which is also called the Pensioner''s Trail because it is more gentle. It saves effort, the marking is better and the path is clearer. We cross the river and go northeast to Golesh Hill. When we get to it, the road begins to climb it in serpentines. After about two hours we reach the highest part of the forest belt. After nearly half an hour we are at the Ravnets hut.
        Red – On the same side of the river is the red route to the Ravnets hut. It goes up through the forest. From time to time he goes out into a clearing, where we have to watch more closely for the markings. After two hours and a little, he leaves the forest belt. The route continues along the Tunkia rut ridge, gradually starting to undercut it from the east. We reach the river valley, where we cross it and make a sharp turn to the right. A little later is the Ravnec hut.
        The first part of the route from Ravnets hut to Botev peak is the most tiring. These two hours are a climb to the Ravnec massif. From the valley in which the hut is located, we head towards Golesh Hill, along which the mark curves upwards. It is shared blue and red for now, and at the beginning it also coincides with the winter wheel markings. After 40-50 minutes we turn left from the winter markings to the gorge of the Karamandra River. The ascent continues steeply along its valley.
        Shortly5-10 minutes after the Bulkata area, we come out at the junction for the Vasil Levski hut. From here the blue marker veers off to the left and descends towards it. We continue along the red road to Duza. We are already finally leaving the forest belt, and one of the most beautiful views on the route opens before us - towards the alpine ridge of Stara Planina. Arranged from left to right are the peaks Levski (Ambaritsa, 2166 m), Kupena (2169 m), Krasttsite (2035 m) and Kostenurkata (2035 m). It is from here that the last peak is in the shape of a turtle, which gives it its name. Our goal, Mount Botev, can also be seen from here. after the sources of the river, the slope decreases and reaches the area of ​​Bulkata, where the summer route briefly intersects with the winter route again. The winter one, however, continues straight east and up towards Ravnets Hill. Ours branches off to the left, northeast direction, to cut the ridge from the north. We get here in about 2 hours and a total of 4:30 from Karlovo.
        Gradually, the slope straightens completely for a long time and is even slightly downward. The trail winds along the hills and crosses three ravines before emerging at the Duza saddle. About 40-50 minutes after the fork for the Vasil Levski hut, we pass a stone building called the Bunkera, which can be used as a shelter. I will point out that it is good to look out for herds of herding dogs in this area.
        After another 10 minutes, we are at the big junction Petolatchka. On the left is the Vasil Levski hut. Straight along the ridge is the winter route to the ridge of Stara Planina. Straight ahead, to the right along the ridge, is our route to Mount Botev. To the right is Rai Hut. To this point, we add another 1:15 hours and the total is already about 5:45 hours.
        The path continues along the green marking, which is shared with the route from Vasil Levski hut to Botev peak. There is almost no slope to Derin dere, the first of three before the Botev shelter. From there on we have easy climbs and then descents to the rest of the gullies and so on to the shelter. After Derin dere we can now walk more calmly from the point of view of the sheepdogs. There isn''t much to describe in this section. Zaslon Botev is like a hut, so in addition to shelter, you can support yourself with food and drinks there. We are here in 1:30 hours from Petolatchka and 7:15 hours from Karlovo.
        Next is the final ascent on the route from Karlovo to Mount Botev. We''ve gotten used to the slight incline, but it''s time to tighten our legs again. The path from here is quite wide and winds in serpentines to the highest point of Stara Planina. We are already following the red marking. Due to accumulated fatigue, the ascent may take about an hour. Upstairs, you can also support yourself with food and drinks in the Botev peak tourist bedroom. Descent can be by one of the other routes for climbing Mount Botev.',
        'SUMMER', 'YES', 5, 1867, 'Karlovo', UTC_TIMESTAMP(), 'APPROVED', 10, 1, null, 'APPROVED'),
       (8, 'Grad Klisura', 'Vrah Vejen', 12,
        'The ascent of Mount Vezhen from the town of Klisura is a rather long and difficult trek from the foot of Stara Planina to one of its highest peaks. 1600 meters of elevation gain are overcome in 12 kilometers! Since we are almost all the time moving outdoors on a steep incline, this can be a very tiring endeavor during the summer months. Still, the old mountain ridge is a nice experience, and it is not easily accessible in this part.',
        'SUMMER', 'YES', 4, 1428, 'Klisura', UTC_TIMESTAMP(), 'PENDING', 10, null, 14, 'PENDING');

INSERT INTO hiking_trails_activity (hiking_trail_id, activity)
VALUES (1, 'HIKING'),
       (1, 'TRAIL_RUNNING'),
       (2, 'HIKING');

INSERT INTO hiking_trails_available_huts(available_huts_id, hiking_trail_id)
VALUES (1, 1),
       (2, 1),
       (3, 3),
       (4, 7),
       (5, 7),
       (6, 7);

INSERT INTO hiking_trails_images(hiking_trail_id, image_id)
VALUES (1, 4),
       (1, 5),
       (1, 6),
       (1, 7),
       (1, 8),
       (1, 9),
       (1, 10),
       (1, 11),
       (1, 12),
       (1, 13),
       (8, 14),
       (8, 15);

INSERT INTO destinations_images(destination_id, image_id)
VALUES (1, 16),
       (2, 17),
       (3, 18),
       (4, 19),
       (5, 20),
       (6, 21),
       (7, 22);

INSERT INTO hiking_trails_liked_by_users(hiking_trail_entity_id, liked_by_users_id)
VALUES (3, 4),
       (4, 4),
       (5, 4);

INSERT INTO hikes (id, start_point, end_point, start_point_coordinates, hike_date, image_url, next_to, hike_info,
                   user_id, hiking_trail_id)
VALUES (1, 'hotel Drujba', 'Pametnik tsar Asen II', null, '2024-05-21', 'https://picsum.photos/200', 'Haskovo',
        'Tsar Ivan Asen II eco-trail is ready to welcome enthusiasts and lovers of ecotourism. The route leads in the footsteps of "Tsar Ivan Asen II" and brings us back to the memory of the glorious battle near the village of Klokotnitsa - one of the most significant events in the history of Bulgaria.',
        1, null),
       (2, 'Mineralni Bani', 'Aykaas', null, '2024-04-10', 'https://picsum.photos/200', 'Mineralni bani', 'The "Mineral baths - Aikaas" eco trail, with a total length of 7.75 km, gives you a perfect opportunity to immerse yourself in the tranquility and beauty of nature, see interesting rock sanctuaries and learn more about the life of the ancient Thracians who inhabited these lands. Information and signposts have been installed along the route of the tourist eco-trail. There are designated recreation areas, outdoor barbecue facilities, sports and recreation attractions in places with unique views.
       There are many cultural-historical and natural attractions along the trail, but among the most impressive are: Locality "Sharapani" - preserved monuments from the Old Iron Age in the area. "Sharapani" (wine stone) are artificially carved hollows in the rocks, made by humans 2600-2800 years ago. Each sharapana consists of two unequal basins with a sloping floor and an open or closed channel. The name "sharapana" comes from the Turkish word "sharap" and means wine. Based on this, we judge that the inhabitants of these places processed grapes for wine in them. Another theory is that Sharapanas were used in ancient times to wash and extract gold as the area was rich in gold deposits.
       The "Aikaas" locality is located on the western slope of the "Mechkovets" mountain hill (Aida peak), immediately below a natural rock ridge. It offers a panoramic view to the west and south, towards the interior of the Rhodopes, which impresses greatly with its beauty and a range of more than 30 km. The forest is deciduous, beech and oak. Nearby is a natural deposit of lily of the valley and wild peony, whose beauty you can enjoy in the spring.
       The fauna is preserved almost virgin - in the forest there is an abundance of different types of birds, roe deer, deer, wild pigs, etc.
       The place "Aikaas" is very beautiful, easily accessible for tourists of all age groups and suitable for family tourism and recreation.',
        1, null),
       (3, 'Boyan Botevo', 'Orlovi Skali', null, '2024-04-11', null, 'Mineralni Bani', 'Real coolness with a breath of fresh mountain air! An idea for a walk with an excursion near Haskovo, around the resort of Mineralni Bani, in the lands of the villages of Boyan Botevo and Sarnitsa, only 30 km away. from the regional town.
       The Boyan Botevo - Ulu Dere - Orlovi Skali tourist eco-trail starts from the village of Boyan Botevo, with a total length of 10 km.
       In many places along the continuation of the eco-trail, as well as in the "Pette Chuchura" and "Orlovi skali" localities, there are corners for relaxation and sports, unique places for peace and quiet, where the air is clean and the view is wonderful. Walking along the Ulu Dere river, you will enjoy the incredible atmosphere created by the intertwined branches of the tall trees above you. You will pass interesting rock phenomena carved over time by the river. One of these places is the so-called "Rock Room" where you can stop and take a breather.
       The rock phenomenon "Eagle Rocks", resembling a huge castle, has majestic rocks reaching up to 30-40 m in height. Numerous trapezoidal niches can be observed there, which are undoubtedly the work of human hands. Thracian rock niches are found in many places in the Eastern Rhodopes. The Thracian fortress in the area is from the Neolithic Age - 1st millennium BC.
       The sights and natural beauties of this place offer you to break away from the noise of the city and satisfy your desire to communicate with nature.',
        1, null),
       (4, 'Mineralni Bani', 'Garvanitsa', null, '2024-06-07', null, 'Minerlani bani', 'The resort village of Mineralni Bani, 18 km away. from Haskovo, represents a touch of a thousand-year history, full of many sights, most of them with the status of protected sites. At the southern end of Mineralni Bani, the tourist eco-trail "Mineralni Bani - Garvanitsa" begins. Along the route of the eco-trail, with a total distance of 5.2 km, there are 5 attractive areas for sports and recreation, suitable for picnics and barbecues.
       The route is easy, like an easy walk and with the possibility of rock climbing on the rocks up to "Garvanitsa" peak. The eco-path also gives you access to cultural values, sanctuaries and mysticism preserved in this part of the territory of the Eastern Rhodopes.
       At the locality "Graduška Church" - a Christianized ancient pagan sacral territory, a tradition from ancient times is preserved. On a rocky platform, around a centuries-old oak, which the locals call "the oak tree", stones are piled up, forming a high circle around the tree. Residents from nearby villages always gather here on the first Thursday after Spasovden for a "Prayer for Rain" ritual, and the miracle really often comes true on the same day.
       The archaeological cultural value "Thracian sanctuary of Garvanitsa" covers the entire hill. A large number of altars, rock cuts, gutters and other sacred objects are carved into numerous rock formations. Interesting rocks where we can assume they were carved in ancient times to be used for sacred rituals. Here, the niches have an egg-shaped shape, and it is assumed that they placed the ashes of the "dead" in them. Archaeologists claim that the entire sanctuary complex served as a necropolis in ancient times.
       On the peak "Golyamo Gradishte" at 555.4 m above sea level, the remains of fortress walls have been preserved. The site is one of the relatively well-preserved ancient Thracian fortresses in Southern Thrace. At the very top there is a wooden platform, from which a beautiful view of the entire surroundings is revealed, and the Bulgarian tribagrenik blows beautifully under the force of the wind. Visibility in all directions over a long distance is excellent. To the south is the Mechkovets ridge, and to the west there is a view of the fortress on Mount Dragoyna.
       Horse riding, archery, sports fishing, etc. can be organized for tourists upon prior request.', 1, null),
       (5, 'Angel Voivoda', 'Hasara', null, '2024-04-15', 'https://picsum.photos/200', 'Mineralni bani', 'Friends and connoisseurs of ecotourism, tourist eco-trail "Angel Voivoda - Thracian sanctuary "Hasara" has a total length of about 2 km.
        Start - the center of the village of Angel Voivoda.
        Duration about 1 hour.
        Moderate difficulty of the trek.
        At the end of the eco-trail, you will reach the Archaeological Thracian Rock Cult Complex and Late Antique Fortress in the Hasara area, which is the largest and richest archaeological site in the Haskovo region. The ancient sanctuary - one of the largest in Bulgaria, has the status of a cultural monument of national importance. The complex is located on a rocky peak, known as "Asara", popularized as the "City of the Sun".
        The archaeological immovable cultural value of the group includes: a rock tomb, a rock altar, a rock sundial - so far the only one in Bulgaria, sharapani, over 70 rock niches, a late antique fortress and an early Christian church with the graves of the "first priests".
        Each one of the listed sites is of high historical and archaeological value, preserving in itself vivid traces of the life, lifestyle, sacred rituals and beliefs of the people who inhabited these lands from the deepest antiquity to early Christianity. You can undoubtedly feel the deep energy of this sanctuary and the magic of Thracian culture, all in one place. The cult complex near the village of Angel Voivoda is part of the unique rock sanctuaries that are characteristic only for the territory of the Eastern Rhodopes and provide valuable information about the most ancient civilizations that inhabited the European continent.',
        1, null),
       (6, 'Malko Gradiste', 'Last waterfall', null, '2024-05-10', null, 'Malko Gradishte',
        'Ecopath The Secret Waterfalls - village of Malko Gradishte, Lubimets municipality. A favorite, picturesque and beautiful path that starts from the church in the village. There are 3 waterfalls here, and the last one can be reached in about 20-30 minutes. Along the way there are built benches for relaxation.',
        1, null),
       (7, 'Harmanli', 'Defileto', null, '2024-05-06', 'https://picsum.photos/200', 'Harmanli',
        '"Defileto" offers a variety of habitats and despite its small area has a rich fauna. Located in the descending slopes of the Rhodope Mountains, the "Defileto" eco-trail has collected in itself the most valuable treasures of our land - unique biodiversity, rich historical past, preserved nature, interesting landscapes, mild climate and breathtaking scenery. The area "Defileto" was declared a protected area in 1973. The purpose of the announcement is the protection of tree species, as well as protected amphibians, reptiles, birds and mammals. The eco-trail is popular for the many opportunities it offers for year-round recreation and weekend outings.',
        1, null),
       (8, 'Padarci', 'Raiski Izgled', null, '2024-04-27', 'https://picsum.photos/200', 'Kardjali', 'The route for the eco-trail starts about 1.5 kilometers west of the village of Padartsi, which is located about 18 kilometers west of Kardjali. The road to the village is good, but there are turns. After passing the village, heading west, there are several bends. The first major turn is to the left and the next to the right. Just at the bend on the right there is a place to leave your car, and in front of you (in the north direction) you will see a narrow path, from where the route itself begins.
        From there, you only follow this path, which is marked with a yellow marker. In general, the road is in one direction and there is almost nowhere to go wrong. You have to walk about 1.6 kilometers and a little over 200 meters of elevation gain. The route is relatively short, but steep from the start and therefore takes about 1 hour each way. At the end of the route you will see some beautiful benches and a unique view over the dam.',
        1, null),
       (9, 'Zheleznitsa', 'Cherni Vrah', null, '2024-08-03', 'https://picsum.photos/200', 'Zheleznitsa',
        'Detailed information about trail could be found be found below. Please review. The meeting point is the bus station in Zheleznista. We are starting the hike at 9.00am ',
        1, 1),
       (10, 'Selo PLana', 'Vrah Vishegrad', null, '2024-09-18', 'https://picsum.photos/200', 'Selo Plana',
        'Detailed information about trail could be found be found below. Please review. For more information on personal.',
        2, 2),
       (11, 'hija Perelik', 'vrah Orfei', null, '2024-07-06', 'https://picsum.photos/200', 'Smolyan',
        'read the info below', 2, 3),
       (12, 'Gara Skakavica', 'Vodopad Skakavica', null, '2024-05-02', null, 'Kustendil',
        'more information in the trail', 2, 4);

INSERT INTO comments(creation_date, id, user_id, message)
VALUES (UTC_TIMESTAMP(), 1, 2, 'I want to join!'),
       (UTC_TIMESTAMP(), 2, 3, 'Me too, can you give us more details!'),
       (UTC_TIMESTAMP(), 3, 2, 'I will bring some friends'),
       (UTC_TIMESTAMP(), 4, 1, 'You are all welcome! The more the merrier! We are starting the hike at 9:00pm'),
       (UTC_TIMESTAMP(), 5, 2, 'Count me in!'),
       (UTC_TIMESTAMP(), 6, 3, 'Me too!!!'),
       (UTC_TIMESTAMP(), 7, 2, 'I will be bringing my dog'),
       (UTC_TIMESTAMP(), 8, 3, 'I love dogs. What breed is you dog?'),
       (UTC_TIMESTAMP(), 9, 2, 'Cane corso'),
       (UTC_TIMESTAMP(), 10, 3, 'Mine too!!'),
       (UTC_TIMESTAMP(), 11, 1, 'I love this trail!!!'),
       (UTC_TIMESTAMP(), 12, 2, 'The view is amazing!!!'),
       (UTC_TIMESTAMP(), 13, 3, 'Warm pancakes are waiting for us at the top:)'),
       (UTC_TIMESTAMP(), 14, 1, ';) the best pancakes after a long walk!!!'),
       (UTC_TIMESTAMP(), 15, 2, 'My favourite part is the view towards Sofia, especially after dark!'),
       (UTC_TIMESTAMP(), 16, 3, 'Great view:)'),
       (UTC_TIMESTAMP(), 17, 1, '!!!!!'),
       (UTC_TIMESTAMP(), 18, 3, 'It looks exciting! Count me in!'),
       (UTC_TIMESTAMP(), 19, 1, 'I am coming too. How can i contact you?'),
       (UTC_TIMESTAMP(), 20, 2, 'This is my email.'),
       (UTC_TIMESTAMP(), 21, 1, 'Hi there. Can i contact you by email?'),
       (UTC_TIMESTAMP(), 22, 1, 'Great place. I recommend'),
       (UTC_TIMESTAMP(), 23, 2, 'Thanks'),
       (UTC_TIMESTAMP(), 24, 3, 'I am visiting next week');

INSERT INTO accommodations_comments(accommodation_id, comment_id)
VALUES (5, 21);

INSERT INTO hiking_trails_comments(hiking_trail_id, comment_id)
VALUES (1, 11),
       (1, 12),
       (1, 13),
       (1, 14),
       (1, 15),
       (1, 16),
       (1, 17),
       (2, 18),
       (2, 19),
       (2, 20);

INSERT INTO hikes_comments(hike_id, comment_id)
VALUES (1, 1),
       (1, 2),
       (1, 3),
       (1, 4),
       (9, 5),
       (9, 6),
       (9, 7),
       (9, 8),
       (9, 9),
       (9, 10);

INSERT INTO destinations_comments(destination_id, comment_id)
VALUES (1, 22),
       (1, 23),
       (1, 24);

INSERT INTO hiking_trails_destinations(hiking_trail_id, destination_id)
VALUES (1, 8);