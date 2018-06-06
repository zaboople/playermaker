This is a wrapper for the Java Midi Sequencer library, intended to bolt on the traditional concepts of notes, rests, bends, and vibrato with the following objectives:

    1 Flexibility
    2 Conciseness
    3 Straightforwardness

Those are in order of priority, so, no, it isn't exactly straightforward. Java 8's "default" interface methods are downright abused for the sake of multiple inheritance. The most commonly used method names consist of a single letter. Much of what I am doing here would be cursed to no end in a business context.

Also note that while Midi is a widely & actively supported industry standard, it is ancient as all get out and only "standard" insomuch as a given implementation cooperates. I'm not pretending to spackle over those bits. Most notable is that Midi strictly separates sound sample generation/modification from real-time performance, limiting nuance to somewhat more than a piano, approaching a saxophone, but failing a guitar. You can probably find even more flexible music-making tools (but no promises about the guitar...).

Works for me.

