X = [1, 2, 5, 8, 10, 20, 50, 70.987];
A = [0.3168,0.3217,0.3065,0.3109,0.3056,0.3052,0.3040,0.3030];
B = [0.2413,0.2299,0.2234,0.2232,0.2203,0.2238,0.2221,0.2223];
C = [0.3209,0.3403,0.3287,0.3221,0.3238,0.3113,0.2975,0.2862];
D = [0.3168,0.3217,0.3065,0.3109,0.3056,0.3052,0.3040,0.3030];
E = [0.3600,0.3426,0.3113,0.3093,0.2829,0.2648,0.2384,0.2365];

figure;
hold on;
title('Error Rate based on Instances Number');
xlabel('Instance Number(K)');
ylabel('Error Rate (%)');
plot(X, A, 'r-', 'LineWidth', 2);
hold on;
plot(X, B, 'b--*', 'LineWidth', 2);
hold on;
plot(X, C, 'k-o', 'LineWidth', 2);
hold on;
plot(X, D, 'm--x', 'LineWidth', 2);
hold on;
plot(X, E, 'g-x', 'LineWidth', 2);
legend('Naive Bayes', 'Decision Tree', 'Random Forest', 'AdaBoost + NB', 'AdaBoost + DT');