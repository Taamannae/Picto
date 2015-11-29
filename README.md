# Picto
HackWestern 2 Submission

## Inspiration
We wanted to create a new way to work with translation for people learning new languages.<br/>
These days, everyone has a mobile phone but little to no time to learn a new language.<br/>
That's where Picto comes in, where you can learn new foreign words in seconds.

## What it does
Picto helps you learn a new language by pointing your camera at an object.<br/>
You can translate it into multiple languages, get the objects definition and have it speak the objects name in the requested language.<br/>
If you don't want to learn a new language but just get the definition of the world, you can do so as well!
<br/><br/>
## How we built it
We used IBM's artificial intelligence API's such as Watson Visual Recognition and AlchemyVision for the object recognition in the image.<br/>
IBM's translation API for translating between two languages.<br/>
WordNix API for word definitions.<br/>
In house Android modules for stuff like text to speech, animations, graphics,application flow.<br/>
Node.JS as the middle service between the Android application and the API's.<br/>
Lastly, IBM's BlueMix to host our Node.JS application.<br/>
<br/><br/>
## Challenges we ran into
The new Android Camera2 module is still in its early ages. There is limited support for the issues we encountered while using it. We solved most issues, we still have a few issues lying around in a few places.<br/>
Another challenge was image sizing and scaling. The visual recognition API we used only supports images less than 1MB, most of our images were captured at the 4-5MB range. We had to play around with the compression and scaling to get the right touch.<br/>
<br/><br/>
## Accomplishments that we're proud of
We're proud of creating the application using the latest Android libraries that had limited support, while adhering to the best Android practices.<br/>
<br/><br/>
## What we learned
Setting up an Android project initially is a huge pain.<br/>
Android has no simple library ready to use for HTTP POST requests.<br/>
Don't use new modules (Camera2) that have limited support during a hackathon!<br/>
<br/><br/>
## What's next for Picto
Improve the visual recognition by possibly improving the images sent over to the API or to look for a new API that's more advanced.<br/>
Add more languages that we can translate for the user.<br/>
Continue improving our Camera2 fragment to make it lightning fast.<br/>
Create a favorites fragment for users to explore their saved words for fast reference.<br/>
Sharing results with family and friends struggling with learning a new language.<br/>
