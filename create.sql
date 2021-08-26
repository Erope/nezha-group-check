CREATE TABLE groupcheck (
                            ID INTEGER PRIMARY KEY autoincrement,
                            QQ BIGINT UNIQUE NOT NULL,
                            HOST VARCHAR(255) NOT NULL UNIQUE,
                            TIME TIMESTAMP default (datetime('now', 'localtime'))
);

CREATE INDEX index_uid
    on groupcheck (QQ);

CREATE UNIQUE INDEX index_host
    on groupcheck (HOST);
