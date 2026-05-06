from sqlalchemy import create_engine, text
from sqlalchemy.orm import sessionmaker, declarative_base

DATABASE_URL = "postgresql://postgres:postgres@localhost:5432/distcomp"

engine = create_engine(
    DATABASE_URL, connect_args={"options": "-c search_path=distcomp,public"}
)
SessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=engine)
Base = declarative_base()


def create_tables():
    with engine.begin() as conn:
        conn.execute(text("CREATE SCHEMA IF NOT EXISTS distcomp"))
        conn.execute(text("ALTER USER postgres SET search_path = distcomp, public"))
    Base.metadata.create_all(bind=engine)

    # Триггер: после удаления связи из tbl_topic_marker удалить маркер, если он не используется
    trigger_sql = """
    CREATE OR REPLACE FUNCTION distcomp.delete_orphan_marker()
    RETURNS TRIGGER AS $$
    BEGIN
        DELETE FROM distcomp.tbl_marker
        WHERE id = OLD.marker_id
          AND NOT EXISTS (
              SELECT 1 FROM distcomp.tbl_topic_marker
              WHERE marker_id = OLD.marker_id
          );
        RETURN OLD;
    END;
    $$ LANGUAGE plpgsql;

    DROP TRIGGER IF EXISTS trg_delete_orphan_marker ON distcomp.tbl_topic_marker;
    CREATE TRIGGER trg_delete_orphan_marker
        AFTER DELETE ON distcomp.tbl_topic_marker
        FOR EACH ROW
        EXECUTE FUNCTION distcomp.delete_orphan_marker();
    """
    with engine.begin() as conn:
        conn.execute(text(trigger_sql))
