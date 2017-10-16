use test;

drop table if exists book;

create table book(
  id int(11) not null auto_increment,
  title varchar(100) not null,
  description varchar(255) not null,
  author varchar(100) not null,
  isbn varchar(20) not null,
  printYear int(11) not null,
  readAlready boolean not null,

  primary key (id));

insert into book (title, description, author, isbn, printYear, readAlready) values
  ('book1',     'book about topic1',         'author1', 'isbn1', 1980, false),
  ('book2',     'book about topic2',         'author1', 'isbn2', 1980, false),
  ('book3',     'book about topic2',         'author2', 'isbn3', 1981, false),
  ('book4',     'book about topic2',         'author3', 'isbn4', 1982, false),
  ('book5',     'book about topic2',         'author4', 'isbn-5', 1983, false),
  ('book6',     'book about topic2',         'author2', 'isbn-6', 1984, false),
  ('book7',     'book about topic2',         'author3', 'isbn-7', 1985, false),
  ('book1',     'another book about topic1', 'author1', 'isbn8', 1986, false),
  ('book2',     'another book about topic2', 'author3', 'isbn9', 1987, false),
  ('book3',     'another book about topic3', 'author5', 'isbn10', 1970, false),
  ('book4',     'book about book2',          'author6', 'isbn11', 1971, false),
  ('book10',    'book about topic10',        'author2', 'isbn-1', 1980, false),
  ('book12',    'book about topic12',        'author4', 'isbn-2', 1980, false),
  ('book13',    'book about topic13',        'author5', 'isbn-3', 1981, false),
  ('book14',    'book about topic14',        'author7', 'isbn-4', 1982, false),
  ('book15',    'book about topic15',        'author6', 'isbn-5', 1983, false),
  ('book16',    'book about topic16',        'author8', 'isbn6', 1984, false),
  ('book17',    'book about topic17',        'author10', 'isbn7', 1985, false),
  ('schmook0',  'book about topic0',         'author-1', 'isbn1', 1980, false),
  ('schmook9',  'book about topic9',         'author-1', 'isbn2', 1980, false),
  ('schmook6',  'book about topic6',         'author-2', 'isbn3', 1981, false),
  ('schmook5',  'book about topic5',         'author-3', 'isbn4', 1982, false),
  ('schmook34', 'book about topic34',        'author-4', 'isbn5', 1983, false),
  ('schmook2',  'book about topic2',         'author2', 'isbn6', 1984, false),
  ('schmook43', 'book about topic2',         'author-3', 'isbn7', 1985, false),
  ('schmook3',  'another book about topic1', 'author1', 'isbn8', 1986, false),
  ('schmook3',  'another book about topic2', 'author-3', 'isbn9', 1987, false),
  ('schmook3',  'another book about topic3', 'author-5', 'isbn10', 1970, false),
  ('schmook4',  'book about book2',          'author6', 'isbn11', 1971, false),
  ('schmook5',  'book about topic10',        'author-2', 'isbn1', 1980, false),
  ('schmook2',  'book about book2',          'author-6', 'isbn11', 1971, false),
  ('schmook3',  'book about topic10',        'author2', 'isbn1', 1980, false);

