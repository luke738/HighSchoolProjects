The start of a program to compute and plot orbits in 3d.
Intended to use a patched-conics approximation but not quite at a point where that becomes relevant.
Current issues lie in performing a manuever (a call to Orbit.perturbOrbit) without the orbit and orbiting body wildly and unphysically changing position. Probably something wrong in the rotations from an equatorial orbit to an orbit with inclination, RAAN, and argument of periapsis.
Uses the LibGDX library for graphics.
