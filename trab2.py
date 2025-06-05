import sys


points = [
	(0.0, 1.6),
	(0.5, 3.0),
	(0.8, 3.7),
	(1.0, 4.8),
	(1.4, 4.7),
	(2.0, 3.0),
	(2.7, 2.6),
]

# points = [(2, 3) ,(4,4), (5,5), (7, 4)]
# points = [(-2, 4), (-1, 1), (1, 1) ]

# Pre calcular o numerador (cada iteracao vamos dividir um ponto)
def numerador_geral(x):
	accum = 1
	for px, _ in points:
		accum = (x - px) * accum 
    
	return accum

# Dado um ponto da nossa lista de pontos
def denominador_geral(index):
    target_point = points[index]
    target_x = target_point[0]
    result = 1
    for i in range(len(points)):
        if index == i: # ignorar ponto target
            continue
        current_point = points[i]
        current_x = current_point[0]
        result = (target_x - current_x) * result


    return result


# print(numerador_geral(3))
# print(denominador_geral(0))



def f(x):
    numerador_g = numerador_geral(x)
    accum_res = 0
    for i, (px, py) in enumerate(points):
        target_point = points[i]
        accum_res += ((numerador_g / (x - target_point[0]) ) / denominador_geral(i)) * target_point[1]
    return accum_res
print(f(2.5))

import matplotlib.pyplot as plt
import numpy as np

# Define the x-range for plotting
x_vals = np.linspace(min(p[0] for p in points) - 0.2, max(p[0] for p in points) + 0.2, 500)
y_vals = [f(x) for x in x_vals]

# Plot the interpolation curve
plt.plot(x_vals, y_vals, label='Interpolated Curve', color='blue')

# Plot the original data points
px_vals, py_vals = zip(*points)
plt.scatter(px_vals, py_vals, color='red', label='Data Points')

plt.xlabel('x')
plt.ylabel('f(x)')
plt.title('Lagrange Polynomial Interpolation')
plt.legend()
plt.grid(True)
plt.show()

