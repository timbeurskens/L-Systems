# L-Systems

Java implementation of a Lindenmayer System parser, including a Turtle Graphics processor

CarpetDesigner can be used to design patterns that look like the Sierpinski Carpet

## Command line arguments
### Carpet designer:
*   -o {file} specify the output file
*   -s {w}x{h} specify the block size
*   -i invert all cells
*   -f show jumps
*   -e show extended jumps
*   {property}={value} settings for output file: see L-System configuration below

### L-System:
*   {file} load specified file

## Configuration
Example configuration file:

```
[rules]
B=[~&TL-B++B]
L=[{-g++g%--g}]
R=!*R
T=Tg

[settings]
axiom=R~B
color=#753326
angle=15
length=10
width=1
length=25
generations=12
color_increment=10
polygon_color=#228B22
turtle_output=false
image_preview=true
image_animation=false
svg_output=true
```
