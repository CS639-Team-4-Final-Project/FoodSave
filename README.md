# Team 4 - Food Waste Management

# App: FoodSave

## Team Members:
1. Charitha Gangaraju
2. Jitendra Rawat
3. Niyati Trivedi
4. Prithvi Karkera

## Overview
FoodSave is an Android application that is designed to address the issue of food wastage by donating extra food and notifying users about available food. This application will help students by sharing food and saving money.

**The link to the Idea Proposal:** 
https://docs.google.com/document/d/1p_MULypPV1GwtT9ycAXt5ETMKdIyZ5uMoMVKL7KNQu0/edit?usp=sharing   

**The link shows the basic workflow of the app:** 
https://drive.google.com/file/d/1C3ykWg1HSSV7b2w08rpMQU8bs27s_yOG/view?usp=drive_link  

## Technologies used:
1.	Android Studio
2.	Kotlin
3.	Firebase
4.	Google Map APIs

## Key Features:
### SignUp:
- Created a Material design-based signup screen with user details and then this data is stored in Firebase Database.
![Sign_Up](https://github.com/CS639-Team-4-Final-Project/FoodSave/assets/61057243/5cbf7348-d2b9-40ce-80a0-898625d87aa6)

### LoginPage:
- Created a Login Page with email id and Password which is authenticated from Firebase Authentication.
![Login](https://github.com/CS639-Team-4-Final-Project/FoodSave/assets/61057243/1d697373-594d-425f-a30c-988b0dd0b6b1)

### Dashboard:
- ⁠The Dashboard screen shows all the different buttons for the user to navigate to different screens like Donate, Receive, FoodMap, Rewards, feedback Form.
![Dashboard](https://github.com/CS639-Team-4-Final-Project/FoodSave/assets/61057243/1d6032fa-2e2c-4f69-b691-12a3ea3d369d)

### Donate Screen:
- The Donate screen would ask the user to provide details of the food item to be donated such as Name of the food, description, and expiry of the food item.
- This data is stored in the backend in Firebase database with the live location of the donor.
![Donate_Screen](https://github.com/CS639-Team-4-Final-Project/FoodSave/assets/61057243/aed3d745-9dda-4af5-817e-77fb54a0def5)

### Food Map Screen:
- This screen would display the google map with Marker on the maps showing exact location and description of the product provided by the Donors.
⁠- It displays the live location of the user and displays the food data near to the live location of the user.
![FoodMap](https://github.com/CS639-Team-4-Final-Project/FoodSave/assets/61057243/8dee4a59-83f2-4c5c-8ab3-43ab2be1d751)

### Receive Screen:
- This screen has a search bar for the user to select a particular food item and check if it is available.
- This data is provided from the Firebase Database which is saved at the donorRecords collection.
- Once the receiver chooses a particular food item, the marker would be removed from the google map and in the firebase, the record of the particular food item will be marked as isAvailable to false.
![Receive_Screen](https://github.com/CS639-Team-4-Final-Project/FoodSave/assets/61057243/9a02462b-7a70-4ca8-83bb-64dcd518ea68)

### Rewards:
- In this section, the user would receive 50 Reward points for each donation made to the app and this would be displayed at the Reward screen with a redeem button where the user can redeem the points.
- The Reward screen also displays the different badges that would be earned on every 100 points.
![Reward_Screen](https://github.com/CS639-Team-4-Final-Project/FoodSave/assets/61057243/539a5d02-c858-42fc-968b-8d61e6807bdc)

### Feedback Form:
- Receiver can provide their feedback and rating for the quality of food received and the donor details.
![Feedback_Form_Screen](https://github.com/CS639-Team-4-Final-Project/FoodSave/assets/61057243/afbb8bd5-94da-47da-9126-ed118b2218fd)

### About Us:

![About_Us_Screen](https://github.com/CS639-Team-4-Final-Project/FoodSave/assets/61057243/10fc9b91-181b-45e4-9e1e-2eb8b30cd40a)

### Contact Us:

![Contact_Us_Screen](https://github.com/CS639-Team-4-Final-Project/FoodSave/assets/61057243/c8ab3c78-997c-4399-ae70-b5056578d4a8)
