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

## Tasks
1. **Determine the Recyclability of a Waste Item** (Simple Task)
   > Capture an image of a waste item and view the classification result (recyclable or not recyclable).
2. **Learn and Apply Proper Recycling Methods for a Recyclable Item** (Moderate Task)
   > View the provided recycling guide for the identified recyclable item and follow the step-by-step instructions.
3. **Submit Feedback to Assess Classification Results** (Complex Task)
   > Provide feedback on the accuracy and usability of the classification results to help improve the system.

## Usage  
> ⚠️ **Note:** This prototype contains **simulated features** and may display **predefined or static outputs**. Some functionalities are limited and do not fully represent the final system.  

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
  
> _Note: Text input for detailed feedback is currently unavailable in the prototype._

- If you choose **not** to leave feedback:
  - You will remain on the result page.
  - You may leave the page using the **navigation bar** (e.g., Home or other sections).

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
- **No Real-Time Processing**
  > The prototype does not perform actual image processing or machine learning classification; all outputs are simulated using predefined results.
- **No Camera or Backend Integration**
  > The prototype does not support real camera functionality, live data processing, or backend services, including data storage for feedback. 
- **Limited Functionality**
  > Key features such as image classification and recycling guide generation are visually demonstrated only and not functionally implemented.
- **Limited Navigation Flow**
  > Navigation is restricted to predefined interactions and may not fully represent all possible user paths; some buttons may not be functional.
- **Limited Feedback Input**
  > While star ratings and feedback tags are interactive, text input is not supported due to prototype platform limitations.
- **No Performance Evaluation**
  > System performance metrics such as processing time, accuracy, and responsiveness cannot be measured in this prototype.

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
