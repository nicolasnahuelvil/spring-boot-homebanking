var app = new Vue({
    el:"#app",
    data:{
        email:"",
        phoneNumber:"",
        password:"",
        firstName: "",
        lastName: "",
        errorToats:null,
        errorMsg: "",
        showSignUp: false,
        showLogin: true,
        showEmailForgotPassword: false,
        showResetPassword: false,
        emailForgotPassword: "",
        resetPwCode: "",
        resetPw1: "",
        resetPw2: "",
    },
    methods:{
        showLoginFn: function (){
          this.showLogin = true;
          this.showEmailForgotPassword = false;
          this.showResetPassword = false;
        },
        showEmailForgotPasswordFn: function (){
            this.showLogin = false;
            this.showEmailForgotPassword = true;
            this.showResetPassword = false
        },
        showEmailForgotPasswordFn2: function (){
            this.showLogin = false;
            this.showEmailForgotPassword = false;
            this.showResetPassword = true;
        },
        signIn: function(event){
            event.preventDefault();
            let config = {
                headers: {
                    'content-type': 'application/x-www-form-urlencoded'
                }
            }
            axios.post('/api/login',`email=${this.email}&password=${this.password}`,config)
                .then(response => window.location.href="/web/accounts.html")
                .catch(() =>{
                    this.errorMsg = "Sign in failed, check the information"
                    this.errorToats.show();
                })
        },
        signUp: function(event){
            event.preventDefault();
            let config = {
                headers: {
                    'content-type': 'application/x-www-form-urlencoded'
                }
            }
            axios.post('/api/clients',`firstName=${this.firstName}&lastName=${this.lastName}&email=${this.email}&phoneNumber=${this.phoneNumber}&password=${this.password}`,config)
                .then(() => { this.signIn(event) })
                .catch(() =>{
                    this.errorMsg = "Sign up failed, check the information"
                    this.errorToats.show();
                })
        },
        showSignUpToogle: function(){
            this.showSignUp = !this.showSignUp;
        },
        formatDate: function(date){
            return new Date(date).toLocaleDateString('en-gb');
        },
        sendEmailResetCode: function (event){
            event.preventDefault();
            let config = {
                headers: {
                    'content-type': 'application/x-www-form-urlencoded'
                }
            }
            axios.post('/api/clients/pre-login',`email=${this.emailForgotPassword}`,config)
                .then(() => { this.showEmailForgotPasswordFn2(event) })
                .catch((err) =>{
                    console.log({err})
                    this.errorMsg = "Sign up failed, check the information"
                    this.errorToats.show();
                })
        },
        changePassword: function (event){
            event.preventDefault();

            if (this.resetPw1 !== this.resetPw2) {
                this.errorMsg = "Las contraseñas deben coincidir"
                this.errorToats.show();
                return
            }

            let config = {
                headers: {
                    'content-type': 'application/x-www-form-urlencoded'
                }
            }
            axios.post('/api/clients/change-pwd',`email=${this.emailForgotPassword}&codeEmailPassword=${this.resetPwCode}&newPassword=${this.resetPw1}`,config)
                .then(() => { this.showLoginFn(event) })
                .catch((err) =>{
                    console.log({err})
                    this.errorMsg = err?.response?.data
                    this.errorToats.show();
                })
        },
    },
    mounted: function(){
        this.errorToats = new bootstrap.Toast(document.getElementById('danger-toast'));
    }
})