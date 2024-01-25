var app = new Vue({
    el:"#app",
    data:{
        clientInfo: {},
        clientAccounts: [],
        clientAccountsTo: [],
        debitCards: [],
        errorToats: null,
        errorMsg: null,
        accountFromNumber: "VIN",
        accountToNumber: "VIN",
        trasnferType: "own",
        amount: 0,
        description: "",
        transactionCode: ""
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
                }),
            axios.get("/api/clients/current/accounts")
            .then((response) => {
                //get client ifo
                this.clientAccounts = response.data;
            })
            .catch((error) => {
                this.errorMsg = "Error getting data";
                this.errorToats.show();
            })
        },
        formatDate: function(date){
            return new Date(date).toLocaleDateString('en-gb');
        },
        checkTransfer: function(){
            this.transactionCode = ""
            if(this.accountFromNumber == "VIN"){
                this.errorMsg = "You must select an origin account";  
                this.errorToats.show();
            }
            else if(this.accountToNumber == "VIN"){
                this.errorMsg = "You must select a destination account";  
                this.errorToats.show();
            }else if(this.amount == 0){
                this.errorMsg = "You must indicate an amount";  
                this.errorToats.show();
            }
            else if(this.description.length <= 0){
                this.errorMsg = "You must indicate a description";  
                this.errorToats.show();
            }else{
                let config = {
                    headers: {
                        'content-type': 'application/x-www-form-urlencoded'
                    }
                }
                console.log('SMS OK')
                axios.post(`/api/pre-transaction?fromAccountNumber=${this.accountFromNumber}&toAccountNumber=${this.accountToNumber}&amount=${this.amount}&description=${this.description}`,config)
                    .then(response => {
                        console.log('SMS OK')
                        this.modal.show();
                        console.log('response', response)

                    })
                    .catch((error) =>{
                        console.log('error', error)
                        this.errorMsg = error.response.data;
                        this.errorToats.show();
                    })


            }
        },
        transfer: function(){
            if(this.transactionCode == ""){
                this.errorMsg = "You must input transaction code";
                this.errorToats.show();
                return
            }

            let config = {
                headers: {
                    'content-type': 'application/x-www-form-urlencoded'
                }
            }
            axios.post(`/api/transactions?transactionCode=${this.transactionCode}&fromAccountNumber=${this.accountFromNumber}&toAccountNumber=${this.accountToNumber}&amount=${this.amount}&description=${this.description}`,config)
                .then(response => {
                    this.modal.hide();
                    this.okmodal.show();
                    console.log('response', response)
                    this.errorToats.hide();
                })
                .catch((error) =>{
                    console.log('error', error)
                    this.errorMsg = error.response.data;
                    this.errorToats.show();
                })
        },
        changedType: function(){
            this.accountFromNumber = "VIN";
            this.accountToNumber = "VIN";
        },
        changedFrom: function(){
            if(this.trasnferType == "own"){
                this.clientAccountsTo = this.clientAccounts.filter(account => account.number != this.accountFromNumber);
                this.accountToNumber = "VIN";
            }
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