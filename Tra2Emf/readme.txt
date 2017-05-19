Simple and small utility to convert tra files into EMF models being instances of our 
generic transition graph meta-model for representing FSAs, CTGs and PAs.

The Eclipse project containts a class Tra2Emf which has a main method.

Arguments should be: <input-dir> <output-dir> [FSA|PA] [true|false]
    - <input-dir> folder containing all the tra-files to be converted
    - <output-dir> folder where the EMF models (xmi-files) should be written to.
    - [FSA|PA] Whether to interprete the automaton as finite state machine (FSA) or probabilistic automaton (PA)
    - [true|false] Whether the automaton is uniform (true) or not (false)