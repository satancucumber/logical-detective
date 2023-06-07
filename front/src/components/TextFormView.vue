<template>
    <div>
        <h5>Текст</h5>
      <p></p>
        <b-form id="textform">
            <b-form-textarea
                id="textarea-auto-height"
                placeholder="Текст сюжета"
                v-model="text"
                rows="10"
                max-rows="20"
            ></b-form-textarea>
          <p></p>
            <b-button @click="save_text"> Сохранить </b-button>
        </b-form>
      <p></p>
        <b-form id="textfield">
            <b-form-textarea readonly
                id="textarea-auto-height"
                placeholder="Текст сюжета"
                v-model="text"
                rows="10"
                max-rows="20"
            ></b-form-textarea>
          <p></p>
            <b-button @click="edit_text"> Изменить </b-button>
        </b-form>
      <p></p>
        <div v-for="formula in formulas" v-bind:key="formula.id">
            <b-row>
                <b-col sm="2">
                  <p></p>
                    {{ formula.logform }}
                </b-col>
                <b-col>
                  <p></p>
                    <b-form>
                        <b-row>
                            <b-col>
                                <b-form>
                                    <b-form-textarea
                                        :readonly="isRead(formula.id)"
                                        id="textarea-auto-height"
                                        placeholder="Описание формулы в тексте"
                                        rows="2"
                                        max-rows="8"
                                        v-model="descriptions[formula.id]"
                                    ></b-form-textarea>
                                  <p v-show="!isRead(formula.id)" style="color: crimson" > {{ alerttext }} </p>
                                </b-form>
                            </b-col>
                            <b-col>
                              <p></p>
                                  <b-button v-show="!isRead(formula.id)" @click="save_description(formula.id)"> Сохранить </b-button>
                                  <b-button :disabled="!isOpen" v-show="isRead(formula.id)" @click="edit_description(formula.id)"> Изменить </b-button>
                            </b-col>
                        </b-row>
                    </b-form>
                </b-col>
            </b-row>
        </div>
      <p></p>
        <b-button @click="toArr"> Сохранить </b-button>
      <p></p>
    </div>
</template>
<script>
    export default {
      props: ['mformulas', 'mtext'],
      data() {
        return {
            indexes: [],
            alerttext : '',
            text: '',
            textarr: [],
            descriptions: {},
            read: [],
            formulas: null
        }
      },
      created() {
        this.formulas = this.mformulas;
        this.mformulas.forEach(f=> {
          this.read.push(Object.assign({}, {id: f.id, val: true}));
          this.descriptions[f.id] = '';
          this.indexes.push(Object.assign({}, {id: f.id, val: [0, 0]}));
        })
        this.textarr = this.mtext;
      },
      methods: {
        save_text() {
            document.getElementById("textform").style.display = "none";
            document.getElementById("textfield").style.display = "block";
        },
        edit_text() {
            document.getElementById("textfield").style.display = "none";
            document.getElementById("textform").style.display = "block";
        },
        getIndex(list, id) {
            for (var i = 0; i < list.length; i++ ) {
                if (list[i].id == id) {
                    return i;
                }
            }
        },
        inText(str) {
            if (this.text.indexOf(str) != -1) {
              return true;
            }
            return false;
        },
        save_description(id) {
            if (this.inText(this.descriptions[id])) {
              var index1 = this.getIndex(this.read, id);
              var index2 = this.getIndex(this.formulas, id);
              var index3 = this.getIndex(this.indexes, id);
              this.read[index1].val = true;
              this.formulas[index2].description = this.descriptions[id];
              this.indexes[index3].val[0] = this.text.toString().indexOf(this.descriptions[id]);
              this.indexes[index3].val[1] = this.text.toString().indexOf(this.descriptions[id]) + this.descriptions[id].length;

            }
            else {
              this.alerttext = 'Описание не содержится в тексте!';
            }
        },
        isOpen() {
          for (var i = 0; i < this.read.length; i++) {
            if (!this.read[i].val) {
              return true;
            }
          }
          return false;
        },
        edit_description(id) {
            if (!this.isOpen()) {
              var index = this.getIndex(this.read, id);
              this.read[index].val = false;
              this.alerttext = '';
            }

        },
        isRead(id) {
            var index = this.getIndex(this.read, id);
            if (this.read[index].val) {
              return true
            }
            return false
        },
        toArr() {
          var indexes = this.indexes.sort((a, b) => a.val - b.val);
          var start = 0;
          var end = this.text.length;
          for (var i = 0; i < indexes.length; i++) {
            end = indexes[i].val[0];
            if (start != end) {
              this.textarr.push(this.text.slice(start, end));
              start = end;
            }
            end = indexes[i].val[1];
            this.textarr.push(this.text.slice(start, end));
            start = end;
          }
          if (end != this.text.length) {
            end = this.text.length;
            this.textarr.push(this.text.slice(start, end));
          }
        },
      },

    }
</script>
<style scoped>
    #textfield {
        display: none;
    };
    #textform {
        display: block;
    };
</style>