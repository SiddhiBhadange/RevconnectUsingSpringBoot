🌐 RevConnect

📌 
Application Overview
RevConnect is a full-stack monolithic social media web application that provides a platform for personal users, businesses, and content creators to connect and interact. Users can create posts, engage with content through likes and comments, build their network through connections and follows, and manage their profiles through an intuitive web interface. The application features responsive design, real-time notifications, personalized feeds, and role-based access control for different user types.

---

🚀 Features
Core Functional Requirements
As a Personal User, I should be able to:
Authentication & Profile
1.	Register and create an account with email, username, and password
2.	Login to my account using email/username and password
3.	Create and edit basic profile with name, bio/about section, profile picture, location, and website link (optional)
4.	View my profile and other users' profiles
5.	Search for users by name or username
6.	Set profile privacy (public/private)
Post Management
7.	Create text posts with optional hashtags
8.	View my posts on my profile
9.	View personalized feed with posts from connections and followed accounts
10.	Edit posts created by me
11.	Delete posts created by me
Social Interactions
12.	Like posts (own and others')
13.	Unlike posts
14.	Comment on posts
15.	View all comments on a post
16.	Delete my own comments
17.	Share/repost content with attribution to original poster
Network Building
18.	Send connection requests to other personal users
19.	Accept or reject incoming connection requests
20.	View pending connection requests (sent and received)
21.	View my connections list
22.	Remove connections
23.	Follow business accounts and creator accounts
24.	Unfollow accounts
25.	View followers list
26.	View following list
Notifications
27.	Receive in-app notifications for connection requests, accepted connections, new followers, likes, comments, and shares
28.	View unread notification count
29.	Mark notifications as read
30.	View notification history
31.	Manage notification preferences (enable/disable by type)
Feed & Discovery
32.	View personalized feed with posts from connections, followed accounts, and own posts
33.	View trending posts/hashtags
34.	Search posts by hashtags
35.	Filter feed by post type or user type
As a Creator/Business Account User, I should be able to:
Note: Creator and Business accounts have all personal user features plus the following:
Enhanced Profile Management
1.	Register as a creator or business account
2.	Create enhanced profile with business/creator name, category/industry, detailed bio, contact information, website and social media links
3.	Add business address (for business accounts)
4.	Add business hours (for business accounts)
5.	Add multiple external links for endorsements/partnerships
6.	Showcase products/services (business accounts)
Advanced Content Creation
7.	Create promotional posts
8.	Add call-to-action buttons to posts ("Learn More", "Shop Now")
9.	Tag products/services in posts
10.	Schedule posts for future posting
11.	Pin important posts to profile
Analytics & Insights
12.	View post analytics (total likes, total comments, total shares, reach/unique viewers)
13.	View follower demographics
14.	View engagement metrics
Business Features
15.	Create business pages
16.	Post updates and announcements
17.	Share promotional content
18.	Interact with customers/followers
19.	Respond to comments on business/creator posts

Standard Functional Scope
•	Authentication & Security
•	Notification System
•	User Interface & Experience
•	Social Interaction Features
•	Network Management
•	Data Management


---

🏗️ Tech Stack

Frontend: Angular, TypeScript, HTML, CSS, Angular Material  

Backend: Spring Boot, Java, REST APIs, Spring Security, JWT  

Database: MySQL  

---

📸 Application Screenshots  
Below are some screenshots of the application interface.

Login Page  
login.png  

Dashboard  
dashboard.png  

Profile Page  
profile.png  

Edit Profile  
edit-profile.png  
<img width="1366" height="768" alt="image" src="https://github.com/user-attachments/assets/7dc55974-d706-4529-954b-eed371c7c7c3" />
<img width="1366" height="768" alt="image" src="https://github.com/user-attachments/assets/4f2553c7-bf02-4768-b3eb-7acedd96e2ee" />
<img width="1366" height="768" alt="image" src="https://github.com/user-attachments/assets/b7a1875d-e911-4f71-b201-44d4e49c4726" />
<img width="1366" height="768" alt="image" src="https://github.com/user-attachments/assets/40279e67-91e6-4953-b52f-be16f09dabd7" />
<img width="1366" height="768" alt="image" src="https://github.com/user-attachments/assets/4b63b8ac-2d6b-49e7-9389-1136c2d63112" />
<img width="1366" height="768" alt="image" src="https://github.com/user-attachments/assets/42b5d520-aa6a-47d0-97e9-7d221206de4b" />
<img width="1366" height="768" alt="image" src="https://github.com/user-attachments/assets/ccafa89a-11fb-49ac-ab3b-222e7aecf679" />
<img width="754" height="943" alt="E" src="https://github.com/user-attachments/assets/8169178e-5673-4c5b-a9ec-f573feebcf33" />


---

🗂 ERD (Entity Relationship Diagram)  

The ERD diagram represents the database structure and relationships between entities such as Users, Profiles, and Business Details.

Example:

erd.png

---

🧪 Testing  

Testing was implemented to ensure system reliability and functionality.

Backend Testing  
JUnit  
Unit tests for controllers and service layers  

Frontend Testing  
Jasmine  
Karma  
