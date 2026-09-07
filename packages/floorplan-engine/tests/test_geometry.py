import pytest

from andalus_floorplan import FloorPlan, Opening, Point, Room, Wall, floorplan_to_svg


def rectangle_room(name="Majlis", width=5.0, height=4.0):
    return Room(
        name=name,
        polygon=(
            Point(0, 0),
            Point(width, 0),
            Point(width, height),
            Point(0, height),
        ),
    )


def test_room_area_and_perimeter():
    room = rectangle_room(width=5, height=4)
    assert room.area == 20
    assert room.perimeter == 18


def test_wall_length():
    assert Wall(Point(0, 0), Point(3, 4)).length == 5


def test_opening_must_fit_wall():
    wall = Wall(Point(0, 0), Point(4, 0))
    with pytest.raises(ValueError):
        Opening(wall_index=0, offset=3.5, width=1.0).validate(wall)


def test_floorplan_summary():
    plan = FloorPlan(
        walls=[
            Wall(Point(0, 0), Point(5, 0)),
            Wall(Point(5, 0), Point(5, 4)),
        ],
        rooms=[rectangle_room()],
    )
    summary = plan.summary()
    assert summary["roomCount"] == 1
    assert summary["totalRoomArea"] == 20
    assert summary["bounds"]["width"] == 5


def test_floorplan_svg_contains_room_name():
    plan = FloorPlan(rooms=[rectangle_room("فناء")])
    svg = floorplan_to_svg(plan)
    assert "فناء" in svg
    assert svg.startswith("<svg")


def test_invalid_units_rejected():
    with pytest.raises(ValueError):
        FloorPlan(units="yards").validate()
