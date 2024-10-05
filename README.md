# Background

This is a wrapper for the Java Midi Sequencer library. Midi operates at "lower" level than classical notes & staves music notation, so my goal was to create a programming library that implements such a notation, but as API's, because I am insane. Here I might mention The Four Kinds Of Easy Which Don't Get Along With Each Other:

    1 Flexible
    2 Concise
    3 Efficient
    4 Straightforward

I've listed those in order of priority for this particular project; so, no, it isn't exactly straightforward. The most commonly used method names are often given a single letter, for brevity.

Worse yet, consider the problem of trying to make a language not known for conciseness (java) achieve nearly the conciseness of a centuries-old, tried & true, highly expressive notation system. Here I am, a confirmed hater of DSL's (domain specific languages) making something very much like a DSL.

Even worse yet, this is the product of my deciphering a somewhat cryptic and often misrepresented (thanks, internet) standard - MIDI - as well as an often unhelpful Java (standard) library that is supposed to give a decent abstraction for working with that standard. There are many wrong turns on that road and I'm still working on that.

And not even worse, but about the same level: While Midi is a widely & actively supported industry standard, it is only "standard" insomuch as a given implementation cooperates. I'm not pretending to spackle over those bits.

And lastly, along those lines of worsiness, Midi strictly separates sound sample generation/modification from real-time performance, limiting nuance to somewhat more than a piano, approaching a saxophone, but arguably failing a guitar. Still, with the right instrument library (not included) you can do some amazing things.

# Usage

Currently compiles just fine for Java OpenJDK 21 LTS, using Ant. JavaDoc documentation explains everything. For runnable examples, refer to the java/test/test/hear directory, and the test.sh script.

