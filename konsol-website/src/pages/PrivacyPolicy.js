import './App.css';

function PrivacyPolicy() {
  return (
    <div className="PrivacyPolicy">

      <p>
        With using Konsol on your device, you accept privacy policy which is explained below.
      </p>
      <p>
        Konsol may collect some data in order to improve app development/app performance.(This data is collected by Google Services, Firebase & Google Analytics) The data we collect can be; your IP address, log information of the app/the device, location information and the information of your device.
      </p>
      <p></p>
      <p>
        Privacy Policy can be changed in the future by the developer.
      </p>
      <p>Konsol uses Google APIs (Google Cloud APIs, Firebase REST APIs, Google Analytics APIs), this means you accept the privacy policy of Google APIs & Firebase Data Processing Terms with using this application. </p>
      <p>Konsol uses your storage to store app data. This can include Oauth2 token, current user info (email, name, surname). Konsol cannot be responsible for misusing app data on your device.</p>
      <a
        className="Info-link"
        href="https://developers.google.com/terms/api-services-user-data-policy"
        target="_blank"
        rel="noopener noreferrer"
        style={{ "color": "#000000" , "text-decoration": "none" }}
      >
        You can look Google API Privacy Policy by clicking here.
      </a>
      <br/>
      <br/>
        <a
        className="Info-link"
        href="https://firebase.google.com/terms/data-processing-terms"
        target="_blank"
        rel="noopener noreferrer"
        style={{ "color": "#000000" , "text-decoration": "none" }}
      >
        You can look Firebase Data Processing Terms by clicking here.
      </a>
    </div>
  );
}

export default PrivacyPolicy;
