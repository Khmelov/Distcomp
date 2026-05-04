from datetime import datetime, timezone

from src.domain.models.models import Writer, News, Label
from src.persistence.stores import (
    NewsSearchCriteria,
    PageRequest,
    label_store,
    news_store,
    writer_store,
)


def test_writer_store_pagination_and_sort(db_session):
    for i in range(3):
        writer_store.save(
            db_session,
            Writer(login=f"u{i}", password="password12", firstname="F", lastname="L"),
        )

    page = writer_store.find_all_page(
        db_session, PageRequest(page=0, size=2, sort_field="login", sort_desc=False)
    )
    assert page.total_elements == 3
    assert len(page.content) == 2
    assert page.content[0].login == "u0"

    page2 = writer_store.find_all_page(db_session, PageRequest(page=1, size=2))
    assert len(page2.content) == 1


def test_news_search_labels_and_writer(db_session):
    w = writer_store.save(
        db_session,
        Writer(login="author", password="password12", firstname="A", lastname="B"),
    )
    lab = label_store.save(db_session, Label(name="sport"))
    news = news_store.save(
        db_session,
        News(
            writer_id=w.id,
            writer=w,
            title="Match report",
            content="Full story here",
            created=datetime.now(timezone.utc),
            modified=datetime.now(timezone.utc),
        ),
    )
    news.labels.append(lab)
    news_store.save(db_session, news)

    crit = NewsSearchCriteria(
        label_ids=[lab.id],
        writer_login="author",
        title_contains="match",
    )
    res = news_store.search_page(db_session, crit, PageRequest())
    assert len(res.content) == 1
    assert res.content[0].title == "Match report"
