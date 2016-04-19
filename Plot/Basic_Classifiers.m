X = [10, 20, 30, 40, 50, 60, 70, 80];
A = [0.3085,0.3071,0.3059,0.3062,0.3045,0.3053,0.3061,0.3079];
B = [0.2244,0.2241,0.2237,0.2242,0.2238,0.2253,0.2262,0.2275];
C = [0.2989,0.2985,0.2982,0.2988,0.2981,0.3001,0.3020,0.3017];

figure;
hold on;
grid;
title('Error Rate based on Instances Number');
xlabel('Instance Number(K)');
ylabel('Error Rate (%)');
plot(X, A, 'r-', 'LineWidth', 2);
hold on;
plot(X, B, 'b--*', 'LineWidth', 2);
hold on;
plot(X, C, 'k-o', 'LineWidth', 2);
legend('Naive Bayes', 'Decision Tree', 'SVM');