import './App.css';

function TermsOfUse() {
  return (
    <div className="TermsOfUse">
      <p>
        With using Konsol on your device, you accept terms of service which is explained below.
      </p>
      <p>
        Konsol must be used only for your work related to Firebase/Firebase Projects/Google Cloud Projects. Konsol cannot be responsible for other uses or misusing.
      </p>
      <p>
        Terms Of Service can be changed in the future by the developer.
      </p>
      <p>
        You accept to obey Google APIs Terms Of Service & Google Cloud Terms Of Service, if any action against Google APIs, immediate action will be taken by Google as explained in Google APIs Terms Of Service, and Konsol cannot be responsible for this action.
      </p>
      <p>You cannot manipulate app data for any purpose, if any action exists, immediate action will be taken by the developer. </p>
      <p>This application is open-source project. This means you cannot sell, distribute or publish the application without notifying the developer, if any action exists for this case, immediate action will be taken.</p>
      <a
        className="TermsOfUse-link"
        href="https://developers.google.com/terms"
        target="_blank"
        rel="noopener noreferrer"
        style={{ "color": "#000000" , "text-decoration": "none" }}
      >
        You can look Google API Terms Of Service by clicking here.
      </a>
      <br/>
      <br/>
      <a
        className="TermsOfUse-link"
        href="https://cloud.google.com/terms/"
        target="_blank"
        rel="noopener noreferrer"
        style={{ "color": "#000000" , "text-decoration": "none" }}
      >
        You can look Google Cloud Terms Of Service by clicking here.
      </a>
    </div>
  );
}

export default TermsOfUse;
