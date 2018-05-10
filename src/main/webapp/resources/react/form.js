class RegForm extends React.Component {

    constructor(props) {
        super(props);
        this.state = {
            username: '',
            password: '',
            subtrendPassword: '',
            email: ''
        };

        this.handleUsernameChange = this.handleUsernameChange.bind(this);
        this.handlePasswordChange = this.handlePasswordChange.bind(this);
        this.handleSubtrendPasswordChange = this.handleSubtrendPasswordChange.bind(this);
        this.handleEmailChange = this.handleEmailChange.bind(this);
    }

    handleEmailChange(event) {
        this.setState({email: event.target.value});
    }

    handlePasswordChange(event) {
        this.setState({password: event.target.value});
    }

    handleSubtrendPasswordChange(event) {
        this.setState({subtrendPassword: event.target.value});
    }

    handleUsernameChange(event) {
        this.setState({username: event.target.value});
    }

    handleSubmit(event) {
        event.preventDefault();
        if(checkFields())
            saveUser();
    }

    render() {
        return (
            <form className="form"  onSubmit={this.handleSubmit}>
                <div className="field">
                    <input type="text" id="username" value={this.state.username} placeholder="Логин" onChange={this.handleUsernameChange} required></input>
                </div>
                <div className="field">
                    <input type="password" id="password" value={this.state.password} placeholder="Пароль" onChange={this.handlePasswordChange} required></input>
                </div>
                <div className="field">
                    <input type="password" id="subtrend_password" value={this.state.subtrendPassword} placeholder="Подтвердите пароль" onChange={this.handleSubtrendPasswordChange} required></input>
                </div>
                <div className="field">
                    <input type="text" id="email" value={this.state.email} placeholder="e-mail" onChange={this.handleEmailChange} required></input>
                </div>

                <div id="recaptcha-border">
                    <div id="g-recaptcha" className="field"></div>
                    <div className="recaptcha-error-message" hidden="true">Докажите, что вы не робот.</div>
                </div>

                <div className="field">
                    <input type="submit" value="Регистрация"></input>
                </div>
            </form>
        );
    }
}

ReactDOM.render(
    <RegForm />,
    document.getElementById('root')
);