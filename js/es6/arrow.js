function foo() {
    setTimeout(() => {
        console.log('id:', this.id);
    }, 100);
}

var id = 21;

foo.call({ id: 42 });

function bar(){
    setTimeout(function(){
        console.log(this)
        console.log('id:',this.id)
    }, 100);
}

bar.call({id:43})