class Tree:
    def __init__(self, name, story, time, lat, lon):
        self.name = name
        self.story = story
        self.time = time
        self.lat = lat
        self.lon = lon

    def tolist(self):
        return {"name": self.name, "story": self.story, "time": self.time, "lat": self.lat, "lon": self.lon}
