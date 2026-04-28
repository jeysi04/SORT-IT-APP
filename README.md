<!-- PROJECT TITLE -->
<div align="center">

<h1>SORT-IT</h1>
<h3><em>Smart Object Recognition Technology for Intelligent Trash Management</em></h3>

<p>
SORT-IT is a mobile application designed to help users identify recyclable waste materials and provide appropriate recycling instructions, supporting proper solid waste management.
</p>

</div>

<!-- FIGMA PROTOTYPE LINK -->
## Publicly Accessible Link
Click here: https://www.figma.com/proto/0GlVO0l6U9LTIGEcBC1uNT/SORT-IT--Final-?node-id=1-2&t=bpQW1regw3MDpgnF-1

<!-- APP OVERVIEW -->
## Features
Below is a brief overview of the SORT-IT user interface:

**Splash Screen**  
The initial interface displayed after launching the application. It serves as a loading screen while the system initializes its components.

**Bottom Navigation Bar**  
A persistent navigation bar located at the bottom of the application that provides access to the main sections:

- **Home**  
  Navigates to the Home page, which displays a welcoming interface, recently opened recycling guides, and an information icon that redirects to the About page.  

  - **About Page**  
    Provides information about the application and contact details for inquiries or support. It includes a horizontal scroll indicator for navigating through sections.

- **Bookmarks**  
  Displays a list of recycling guides saved by the user.

- **Camera**  
  Opens the camera interface used for capturing images of waste materials for classification.

  - **Loading / Image Processing Page**  
    Appears after capturing an image and remains visible until processing is complete.  

  - **Classification Result Page**  
    Displays the classification result, including material and subtype. It also prompts the user to either view recycling methods or provide feedback.  

    > _Note: Recycling methods are only available for waste classified as recyclable. Disposal methods for non-recyclable waste are not provided._

  - **Recycling Methods Selection Page**  
    Displays a list of available recycling guides for the identified recyclable item.

  - **Recycling Guide Page**  
    Provides detailed, step-by-step instructions on how to properly recycle or repurpose the identified recyclable item.

- **FAQs**  
  Displays frequently asked questions using an expandable list format for quick access to common concerns and issues.

- **Feedback**  
  Allows users to rate their app experience using a star rating and select predefined tags. A text input field is available for additional comments, with a 200-character limit indicator that turns red when the limit is reached.

## Usage  
> **Note:** The SORT-IT prototype contains **simulated features** and may display **predefined or static outputs**. Some functionalities are limited and do not fully represent the final system.  

Follow these steps to use the SORT-IT prototype:  
### Capture and Classify a Waste Item
1. Tap the **camera button** in the center of the bottom navigation bar to open the camera interface.  
2. Capture an **image** of the waste item by tapping the capture button.  
3. Wait for the loading screen to appear.  
4. **Tap the loading screen once** to proceed to the classification result.  

> _Note: The loading screen does not automatically redirect. You must tap the screen to continue._

---

### View Results and Recycling Guide
- The result page displays the identified material and classification.
- Since the result is classified as recyclable (simulated), you will be prompted to view the recycling methods:
  + **Yes** → Displays available recycling methods  
  + **No** → Prompts you to provide feedback  

#### Viewing Recycling Methods
- A list of available recycling methods will be displayed.  
- Tap a method to view its **step-by-step instructions**.  
- You may save a method by tapping the **bookmark icon** beside it.  
- Saved guides can be accessed later from the **Bookmarks page**.  

> _Note: Bookmark functionality is simulated in this prototype. Actions such as adding or removing guides are not saved and will not be reflected on the Bookmarks page._

---

### Submit Feedback
- If you choose to leave feedback:
  1. Give a **star rating** based on the result.  
  2. Select applicable **feedback tags** (e.g., accuracy, ease of use).
  3. Review your selected inputs before submission.  
  
> _Note: Text input for detailed feedback is unavailable in the prototype._

- If you choose **not** to leave feedback:
  - You will remain on the result page.
  - You may leave the page using the **navigation bar** (e.g., Home or other sections).

## Tasks
1. **Determine the Recyclability of a Waste Item** (Simple Task)
   > Capture an image of a waste item and view the classification result (recyclable or not recyclable).
2. **Learn Proper Recycling Methods for a Recyclable Item** (Moderate Task)
   > Select and view a recycling guide for the identified recyclable item, then review its step-by-step instructions to understand the proper recycling process.
3. **Submit Feedback to Assess Classification Results** (Complex Task)
   > Provide feedback on the accuracy and usability of the classification results to help improve the system.

<!-- PROTOTYPE INFORMATION -->
## Contextual Information for the Evaluator

### Target Population
- Primary: Residents and homeowners who segregate waste at the household level and have limited knowledge of recycling practices
- Secondary: Waste collectors and junk shop operators who regularly handle recyclable materials
- Others: Individuals interested in proper waste management and environmental sustainability

### When to use SORT-IT?
SORT-IT can be used in everyday situations where users need quick guidance on proper waste disposal, such as:
- When unsure if a waste item is recyclable or not
- Before disposing of household waste (e.g., bottles, paper, plastic items)
- When learning proper recycling methods for specific materials
- When practicing responsible and sustainable waste disposal habits
- During school activities or environmental awareness programs

### What should a user be able to accomplish in this prototype?
The prototype allows users to:
- Navigate the application interface and access different sections through the bottom navigation bar.
- Simulate waste image recognition by accessing the camera interface and tapping the capture button to trigger a classification simulation.
- View predefined classification results indicating whether an item is recyclable or non-recyclable.
- View a list of recycling methods for recyclable items and select among available options.
- Provide feedback by selecting a star rating and tapping feedback tags on the feedback page.

### Limitations  
- **Simulated System Behavior**  
  > The prototype does not perform actual image processing or machine learning classification. All outputs are simulated using predefined results, and core features are visually demonstrated only.

- **No Camera or Backend Integration**  
  > Real camera functionality, live data processing, and backend services (e.g., data storage for feedback) are not implemented.

- **Limited Navigation and Interactions**  
  > Navigation is restricted to predefined flows and may not represent all possible user paths. Some buttons and interface elements may be non-functional or behave differently from a fully developed system.

- **Limited Feedback Functionality**  
  > Feedback is limited to star ratings and predefined tags. Text input is not supported, and submitted feedback is not stored.

- **Prototype Platform Constraints**  
  > Certain interface elements are limited by the prototyping platform:
  > - **Text Input**: Not supported (e.g., Bookmarks search bar and Feedback text field)  
  > - **Loading / Image Processing Screen**: Requires manual tapping to proceed; it does not automatically redirect the user to the result page  
  > - **Bookmarking**: Actions are simulated and are not saved; changes will not appear in the Bookmarks page

- **Unavailable System Feedback**  
  > Pop-up messages (e.g., toast notifications for invalid actions or system alerts) are part of the intended system but do not appear in the interactive prototype due to limited interaction flow.

- **No Performance Evaluation**  
  > System metrics such as processing time, accuracy, and responsiveness cannot be measured in this prototype.

<!-- PROJECT TEAM MEMBERS -->
## Project Team
This project was developed by a group of BS Computer Science students from Bicol University College of Science.
| **Name**                | **Role(s)**                                       |
|-------------------------|---------------------------------------------------|
| A Z Rain L. Espinas     | Project Manager, Developer Support                |
| Ginno L. Buenaobra      | Quality Assurance, Developer Support              |
| Janna Carla R. Morcozo  | Backend Developer, Requirements Analyst           |
| Jaycee D. Cadag         | Frontend Developer, UI/UX Designer                |
| John Melrick M. Loviña  | Frontend Developer, UI/UX Designer                |
