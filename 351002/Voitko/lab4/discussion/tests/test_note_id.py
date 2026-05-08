from discussion_mod.domain.note_id import next_note_id


def test_next_note_id_monotonic_per_thread():
    a = next_note_id()
    b = next_note_id()
    assert b > a
