<template>
    <div>
        <h5>Литералы</h5>
        <p></p>
        <vue-good-table
            :columns="columns"
            :rows="mliterals"
            class="table"
        >
            <template slot="table-row" slot-scope="props">
                  <span v-if="props.column.field == 'before'">
                    <b-button variant="primary" @click="edit(props.row.id)">Изменить</b-button>
                    <b-button variant="primary" @click="del_row(props.row.id)">Удалить</b-button>
                  </span>
                  <span v-else-if="props.column.field == 'suspect'">
                      <b-button variant="primary" @click="change_suspect(props.row.id)">Изменить</b-button>
                      <b-checkbox disabled v-model="props.row.suspect" type=boolean @click="change_suspect(props.row.id)"></b-checkbox>
                  </span>
                  <span v-else>
                    {{props.formattedRow[props.column.field]}}
                  </span>
            </template>
        </vue-good-table>
        <p></p>
        <b-button center variant="primary" @click="add_literal">Добавить</b-button>
        <p></p>
        <b-form inline id="form">
            <label class="sr-only" for="inline-form-input-name">Literals</label>
            <b-form-input
                v-model="name"
                id="inline-form-input-name"
                class="mb-2 mr-sm-2 mb-sm-0"
                placeholder="Литерал"
            ></b-form-input>
            <p></p>
            <b-form-input
                v-model="description"
                id="inline-form-input-name"
                class="mb-2 mr-sm-2 mb-sm-0"
                placeholder="Описание"
            ></b-form-input>
            <p></p>
            <b-form-checkbox
              id="checkbox-1"
              v-model="suspect"
              name="checkbox-1"
              type=boolean
            >
                <span>
                      Подозреваемый
                </span>
            </b-form-checkbox>
            <p></p>
            <b-button variant="primary" @click="save">Сохранить</b-button>
            <p></p>
        </b-form>
    </div>
</template>
<script>
    export default {
        props: ['literals'],
        data() {
            return {
                mid: 1,
                mliterals: [],
                id: null,
                name: '',
                description: '',
                suspect: false,
                text: 'текст',
                columns: [
                    {
                      label: 'Before',
                      field: 'before'
                    },
                    {
                      label: 'Литерал',
                      field: 'name'
                    },
                    {
                      label: 'Описание',
                      field: 'description'
                    },
                    {
                      label: 'Подозреваемый',
                      field: 'suspect',

                    }
                  ]
            }
        },
        created() {
            this.mliterals = this.literals;
        },
        methods: {
            getIndex(list, id) {
                for (var i = 0; i < list.length; i++ ) {
                    if (list[i].id == id) {
                        return i;
                    }
                }
            },
            clean() {
                document.getElementById("form").style.display = "none";
                this.id = null;
                this.name = '',
                this.description = '',
                this.suspect = false
            },
            add_literal() {
                document.getElementById("form").style.display = "block";
            },
            setId(literal, id) {
                literal.id = id;
            },
            save() {
                var literal = {
                    id: this.id,
                    name: this.name,
                    description: this.description,
                    suspect: this.suspect
                };
                if (this.id) {
                    var index = this.getIndex(this.mliterals, this.id);
                    this.mliterals.splice(index, 1, literal);
                    this.put_text('Изменено');
                } else {
                    this.setId(literal, this.mid++);
                    this.mliterals.push(literal);
                    this.put_text(this.id);
                }
                this.clean();
            },
            put_text(text) {
                this.text = text;
            },
            edit(id) {
                document.getElementById("form").style.display = "block";
                var index = this.getIndex(this.mliterals, id);
                this.name = this.mliterals[index].name;
                this.id = this.mliterals[index].id;
                this.description = this.mliterals[index].description;
                this.suspect = this.mliterals[index].suspect;
                this.put_text('Изменить');
            },
            del_row(id) {
                var index = this.getIndex(this.mliterals, id);
                this.mliterals.splice(this.mliterals.indexOf(this.mliterals[index]), 1);
                this.put_text('Удалить');
            },
            change_suspect(id) {
                var index = this.getIndex(this.mliterals, id);
                this.mliterals[index].suspect = !this.mliterals[index].suspect;
            }
        }
    };
</script>
<style scoped>
    #form {
        display: none
    }
</style>