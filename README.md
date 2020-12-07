# chomp

Chomp is a social restaurant sharing and finding app that allows users to search for and make
plans to visit popular restaurants all around the world.  
It gives you complete details about the popular restaurants in your area and helps you in finding the best one for your taste and budget.  

Link to video demo: https://www.youtube.com/watch?v=qNN8UPb8x8Q&feature=youtu.be  
  
Third Party APIs Used:  
1. ZomatoAPI  
2. FirebaseAPI  
  
Android features Used:  
1. Navigation controller  
2. Room Database  
3. Recyclerviews   
4. Livedata  
  
Third party libraries used:  
Gson:  
Converted JSON strings into their Java objects, used for fetching and parsing results from the API. This was pretty standard to what we used for Reddit. Understanding the structure of the API results and converting them to Java objects was a bit of challenge.  
  
Retrofit:  
Helped build the URL for each network request. Like Gson, Retrofit helped us in makingnecessary API calls to pull the data from third party API.  
  
Glide:  
Image loading and management for showing preview images for each restaurant. There wasn’t much challenge about Glide, the majority restaurants in Zomato had images associated, but we had to deal with some restaurants that do not have images.  
  
Room Database:  
Room database is something new in our project. It provides an abstract layer on top of Sqlite database. This was one of the challenging things in the whole project. We used Roomdb to cache the 100 most popular cities in the world, so that the user could search for a city hassle free. It powers an AutocompleteTextview which helps users in autocompleting the City name. It also helps to store user preferences like the Default location.  
  
Third party services used:  
Firestore:  
Allowed us to store our chat history with other users online, so we are able to retrieve the history every time we visit the chat, even if the app has been closed. We had some trouble with making it so that one person could initiate a conversation with only one other person, instead of the chat being visible to everyone who logs on the app.  

Firebase Authentication:  
Requires users to make an account and log in with their email or Google account before accessing the app. We customize the welcome page so that it includes our logo as well as a custom color scheme.  

Noteworthy UI/UX/Display code:  
Using multiple recyclerviews (Horizontal for displaying collections of restaurants and Vertical for displaying restaurants) was a bit of challenge. Modifying the Firebase authentication page to display custom themes and Image was also something new.  

Noteworthy backend/processing logic:  
We believe we are using the right backend for each feature. Majority of the data comes directly from the API. But, we ran into some challenges as the API has a lot of limitations.  
For example, we cannot pull more than 20 restaurants in an API call, this has limited our ability to filter the restaurants by collection and had to make a new API call when a collection is clicked.  
API could not be queried using city name, so we used Room database to cache the list of cities locally and query API with a corresponding city ID every time when a user changes the city. We could do this by saving the city list in a local Repository class, but that is not scalable and would limit the capabilities of the app.  

Most important/interesting thing learned:  
We learned a few new concepts that hadn’t been introduced in class before. Notably, we learned to use roomdb, the navigation controller and opening images in the camera gallery instead of the camera itself. With more time, I think we would have been able to expand upon this functionality to also add saving things like reviews and profile pictures so that it isn’t removed when the app closes, and adding functionality so that the user doesn’t have to input their city manually, but can use location tracking services to automatically guess their current city.  

Difficult Challenges & Debugging Stories:  
1. RoomDB stuff was certainly a challenge. It expects certain dependencies to be added to Gradle file and those dependencies are different for Java and Kotlin  
2. AppCompleteTextview was another challenge. It was easy to populate a list using a local string array. However, bringing this data from a Room database table and show that list using an Arrayadapter was quite challenging  .


