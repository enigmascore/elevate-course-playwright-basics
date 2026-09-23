-- The seed EVERY fresh backend starts from ( e2e profile: create-drop + this file ).
-- Passwords: alice@example.com / bookworm, bob@example.com / pageturner.

insert into users ( id, name, email, password_hash, activated )
values ( 1, 'Alice', 'alice@example.com',
         '$2y$10$EfUl7lGX19hdS1IZW3i45ezMu6B4rc5O0lwoJHMUmzdK27kmY5nmK', true ),
       ( 2, 'Bob', 'bob@example.com',
         '$2y$10$uRB9aDwZYWl5K/98yvZbMuWZftl0zlGD.fpgo9J3/UtmsjdEohOaO', true )
on conflict ( id ) do nothing;

-- three cooks; the six recipes are spread over them 3 / 2 / 1
insert into cooks ( id, name, email, level )
values ( 1, 'Maria Rossi', 'maria@example.com', 'PRO' ),
       ( 2, 'Ken Tanaka', 'ken@example.com', 'HOME' ),
       ( 3, 'Priya Nair', 'priya@example.com', 'HOME' )
on conflict ( id ) do nothing;

insert into recipes ( id, title, cuisine, servings, vegetarian, cook_id )
values ( 1, 'Spaghetti Carbonara', 'Italian', 4, false, 1 ),
       ( 2, 'Margherita Pizza', 'Italian', 2, true, 1 ),
       ( 3, 'Risotto ai Funghi', 'Italian', 4, true, 1 ),
       ( 4, 'Chicken Katsu Curry', 'Japanese', 2, false, 2 ),
       ( 5, 'Miso Soup', 'Japanese', 4, true, 2 ),
       ( 6, 'Chana Masala', 'Indian', 6, true, 3 )
on conflict ( id ) do nothing;

insert into shopping_items ( id, item, quantity, for_recipe )
values ( 1, 'Guanciale', '150 g', 'Spaghetti Carbonara' ),
       ( 2, 'Pecorino Romano', '100 g', 'Spaghetti Carbonara' ),
       ( 3, 'Chickpeas', '2 tins', 'Chana Masala' ),
       ( 4, 'Miso paste', '1 tub', 'Miso Soup' ),
       ( 5, 'Mozzarella', '250 g', 'Margherita Pizza' )
on conflict ( id ) do nothing;

select setval( 'users_seq', 100 );
select setval( 'cooks_seq', 100 );
select setval( 'recipes_seq', 100 );
select setval( 'shopping_items_seq', 100 );
