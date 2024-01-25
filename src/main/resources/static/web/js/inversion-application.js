var app = new Vue({
    el:"#app",
    data:{
        clientInfo: {},
        inversions: [],
        clientAccounts: [],
        accountNumber: "VIN",
        errorToats: null,
        errorMsg: null,
        accountFromNumber: "VIN",
        amount: 0,
        inversionId: 0,
        amountInversion: 0,
        resInversion: "",
        date:"",
    },
    methods:{
        getData: function(){
           axios.get("/api/clients/current")
               .then((response) => {
                   //get client ifo
                   this.clientInfo = response.data;
               })
               .catch((error)=>{
                   // handle error
                   this.errorMsg = "Error getting data";
                   this.errorToats.show();
               })
            Promise.all([axios.get("/api/inversions"),axios.get("/api/clients/current/accounts")])
            .then((response) => {
                //get loan types ifo
                this.inversions = response[0].data;
                this.clientAccounts = response[1].data;
            })
            .catch((error) => {
                this.errorMsg = "Error getting data";
                this.errorToats.show();
            })
        },
        formatDate: function(date){
            return new Date(date).toLocaleDateString('en-gb');
        },
        checkApplication: function(){

        this.date = new Date();

        if(this.accountFromNumber == "VIN"){
                this.errorMsg = "You must indicate an account";
                this.errorToats.show();
        } else if(this.inversionId == "VIN"){
                this.errorMsg = "You must select an option";
                this.errorToats.show();
        } else if(this.amountInversion == 0){
                this.errorMsg = "You must indicate an amount";
        } else{
                this.modal.show();
            }
        },
        apply: function(){
            axios.post("/api/inversions",{accountNumber: this.accountFromNumber, inversionOptionId: this.inversionId, initialAmount: this.amountInversion, initialDate: this.date})
            .then(response => {
                this.modal.hide();
                this.okmodal.show();
            })
            .catch((error) =>{
                this.errorMsg = error.response.data;
                this.errorToats.show();
            })
        },
        changedType: function(){
            this.inversions.forEach (function(id, i, array){
                if(inversionId == id){
                    this.resInversion = this.inversions[i].macroName;
                }
            })
        },
        finish: function(){
            window.location.reload();
        },
        signOut: function(){
            axios.post('/api/logout')
            .then(response => window.location.href="/web/index.html")
            .catch(() =>{
                this.errorMsg = "Sign out failed"
                this.errorToats.show();
            })
        },
    },
    mounted: function(){
        this.errorToats = new bootstrap.Toast(document.getElementById('danger-toast'));
        this.modal = new bootstrap.Modal(document.getElementById('confirModal'));
        this.okmodal = new bootstrap.Modal(document.getElementById('okModal'));
        this.getData();
    }
})