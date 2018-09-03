This is a wrapper for the Java Midi Sequencer library. Midi operates at "lower" level than classical notes & staves music notation, so my goal was to create a programming library that implements such a notation, but as API's, because I am insane. Here I might mention The Three Kinds Of Easy Which Don't Get Along With Each Other:

    1 Flexible
    2 Concise
    3 Straightforward

I've listed those in order of priority for this particular project; so, no, it isn't exactly straightforward. Java 8's "default" interface methods are downright abused for the sake of multiple inheritance. The most commonly used method names consist of a single letter. Some of what I've done would be cursed to no end in a business context.

Worse yet, consider the problem of trying to make a language not known for conciseness (java) achieve nearly the conciseness of a centuries-old, tried & true, very expressive notation system. Here I am, a confirmed hater of DSL's (domain specific languages) making something very much like a DSL.

Even worse yet, this is the product of my deciphering a somewhat cryptic and often misrepresented (thanks, internet) standard - MIDI - as well as an often unhelpful Java library that is supposed to give a decent abstraction for working with that standard. There were many wrong turns on that road.

And not worst of all, but worth noting: While Midi is a widely & actively supported industry standard, it is only "standard" insomuch as a given implementation cooperates. I'm not pretending to spackle over those bits. Most notable (a pun) is that Midi strictly separates sound sample generation/modification from real-time performance, limiting nuance to somewhat more than a piano, approaching a saxophone, but arguably failing a guitar. Still, with the right instrument library (not included) you can do some amazing things.

Works for me.

