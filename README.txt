In this project I have implemented the code for Counting the frequency of all the Alpha-numerics in "Pride and Prejudice" and then printed the 25 most occuring words. The catch here is that every program has been written under various constraints. 
This is inspired from the book "Exercises in Programming Style" written by Prof. Cristina Videira Lopes; which I have referred extensively for this project. 

Currently uploaded styles.

Style1 - In this style you can't create identifiers except for FileReaders and FileWriters, and the RAM memory is very expensive; hence we try to load as less items as possible in the memory. The implementation is done in Java. 

Style2 - In this style, we are trying to emulate the Forth Programming Style. Where there is a data stack. All operations (conditionals, arithmetic, etc.) are done over data on the stack. There exists a heap for storing data that’s needed for later operations. The heap data can be associated with names (i.e. variables). As said above, all operations are done over data on the stack, so any heap data that needs to be operated upon needs to be moved first to the stack and eventually back to the heap. Abstraction is allowed in the form of user-defined “procedures” (i.e. names bound to a set of instructions), but data transfer between procedures need to happen via Stack ONLY. Heap is just to store intermediate values. The implementation is done in Java.

Style3 - In this style, we are now allowed the use of variables, but we aren't allowed to create abstractions or procedures. The use of libraries needs to be as minimal as possible. The implementation is done in Java. 