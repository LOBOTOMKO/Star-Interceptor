import ephem
import datetime
from datetime import datetime, timezone




def planet_coordinate(planet_name,lati,long,elev):
        # Create an observer object with latitude, longitude, and elevation
        observer = ephem.Observer()
        observer.lat = str(lati)
        observer.long = str(long)
        observer.elevation = (elev)

        # Convert the input date to a datetime object
        now = datetime.now(timezone.utc)
        dt = datetime.strptime(now.strftime("%Y-%m-%d %H:%M:%S"), "%Y-%m-%d %H:%M:%S")

        # Set the observer's date and time
        observer.date = dt

        planet_name = planet_name.capitalize()

        try:
                # Create a planet object with the given planet name
                planet = getattr(ephem, planet_name)(observer)
                planet.compute(observer)

                # Calculate the planet's altitude and azimuth
                alt = float(planet.alt * 180 / ephem.pi)
                az  = float(planet.az * 180 / ephem.pi)

        except:
                # Create a planet object with the given planet name
                planet = getattr(ephem, "star")(planet_name)

                planet.compute(observer)
                # Calculate the planet's altitude and azimuth
                alt = float(planet.alt * 180 / ephem.pi)
                az  = float(planet.az * 180 / ephem.pi)

        try:

                mag = planet.mag

        except:
                mag = 0.0

        return alt,az,mag

